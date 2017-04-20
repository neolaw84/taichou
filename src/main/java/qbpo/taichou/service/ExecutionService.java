package qbpo.taichou.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.FileDataset;
import qbpo.taichou.repo.Workflow;
import qbpo.taichou.repo.WorkflowExecution;

@Service
@EnableBatchProcessing
public class ExecutionService implements JobExecutionListener, StepExecutionListener{

	private static final Log log = LogFactory.getLog(ExecutionService.class);

	@Autowired
	private WorkflowService workflowService;

	////////////////////-- Batch -- //////////////////////////////////

	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	@Qualifier("asyncJobLauncher")
	private JobLauncher jobLauncher;

	@Bean
	public JobLauncher asyncJobLauncher() {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return jobLauncher;
	}

	//////////////////////// -- Services -- ///////////////////////////////////////

	private JobExecution startJobExecution (WorkflowExecution workflowExecution) 
			throws JobExecutionException {
		JobExecution answer = null;

		// trying to retrieve job

		Job job = null;

		try {
			job = jobRegistry.getJob(Long.toString(workflowExecution.getWorkflow().getId()));
		} catch (NoSuchJobException e) {
			Utils.logError(log, e, "Unable to retrieve job using workflow id.");
			throw e;
		}

		// building job parameters

		JobParameters jobParameters = Utils.buildJobParameter(workflowExecution);

		// starting job execution

		try {
			answer = jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			Utils.logError(log, e, "Unable to start the workflow execution.");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return answer;
	}

	//@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution queue(Workflow workflow, FileDataset fileDataset) 
			throws JobExecutionException {

		WorkflowExecution answer = workflowService.nullToQueue(workflow, fileDataset);

		JobExecution jobExecution = startJobExecution(answer);
		log.trace("Execution : " + jobExecution.getId() + " started.");
		return answer;
	}

	//////////////////////// -- Callbacks -- //////////////////////////////////////

	@Override
	public void beforeJob(JobExecution jobExecution) {
		Long workflowExecutionId = jobExecution.getJobParameters()
				.getLong(Constants.BATCH_KEY_WORKFLOW_EXECUTION_ID);

		Long jobExecutionId = jobExecution.getId();

		String fileDatasetPath = workflowService.getWorkflowExecutionFileDatasetPath(workflowExecutionId);

		jobExecution.getExecutionContext().putString(Constants.BATCH_KEY_FILE_DATASET_PATH, fileDatasetPath);

		try {
			workflowService.queueToRunning(workflowExecutionId, jobExecutionId);
		} catch (JobExecutionException e) {
			Utils.logError(log, e, "Something wrong in beforeJob aspect.");
			e.printStackTrace();
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		Long workflowExecutionId = jobExecution.getJobParameters().getLong(Constants.BATCH_KEY_WORKFLOW_EXECUTION_ID);

		try {
			workflowService.runningToDone(workflowExecutionId, 
					jobExecution.getExitStatus() == ExitStatus.COMPLETED);
		} catch (Exception e) {
			Utils.logError(log, e, "Something wrong in afterJob aspect.");
			e.printStackTrace();
		}
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		String toAppend = String.join("", 
				"Step ", stepExecution.getStepName(), " has started.");
		
		Long workflowExecutionId = stepExecution.getJobParameters().getLong(Constants.BATCH_KEY_WORKFLOW_EXECUTION_ID);

		try {
			workflowService.progress(workflowExecutionId, toAppend);
		} catch (Exception e) {
			Utils.logError(log, e, "Something wrong in beforeStep aspect.");
			e.printStackTrace();
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		String toAppend = stepExecution.getExecutionContext().getString(Constants.BATCH_KEY_STEP_OUTPUT);
		
		Long workflowExecutionId = stepExecution.getJobParameters().getLong(Constants.BATCH_KEY_WORKFLOW_EXECUTION_ID);

		try {
			workflowService.progress(workflowExecutionId, toAppend);
		} catch (Exception e) {
			Utils.logError(log, e, "Something wrong in afterStep aspect.");
			e.printStackTrace();
		}
		
		return null;
	}

}
