package qbpo.taichou.service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.FileDataset;
import qbpo.taichou.repo.Op;
import qbpo.taichou.repo.Task;
import qbpo.taichou.repo.TaskRepo;
import qbpo.taichou.repo.Workflow;
import qbpo.taichou.repo.WorkflowExecution;
import qbpo.taichou.repo.WorkflowExecution.Status;
import qbpo.taichou.repo.WorkflowExecutionRepo;
import qbpo.taichou.repo.WorkflowRepo;

@Service
public class WorkflowService {

	private static final Log log = LogFactory.getLog(WorkflowService.class);

	private static Map<String, Op> ops = null;

	@Autowired
	ObjectMapper jacksonObjectMapper;

	@Autowired
	TaskRepo taskRepo; 

	@Autowired
	WorkflowRepo workflowRepo;

	@Autowired
	WorkflowExecutionRepo workflowExecutionRepo;

	//////////////////// -- Batch -- //////////////////////////////////

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	@Qualifier("asyncJobLauncher")
	private JobLauncher jobLauncher;

	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	@Qualifier("customJobRegistryBeanPostProcessor")
	private JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor;

	@Bean
	public JobLauncher asyncJobLauncher() {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return jobLauncher;
	}

	@Bean
	public JobRegistryBeanPostProcessor customJobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor bpp = new JobRegistryBeanPostProcessor();
		bpp.setJobRegistry(jobRegistry);
		return bpp;
	}

	//////////////////////////////////////////////////////////

	@PostConstruct
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void init() {

		Reflections reflections = new Reflections(Constants.OP_PACKAGE);
		Set<Class<? extends Task>> taskClasses = reflections.getSubTypesOf(Task.class);
		WorkflowService.ops = new LinkedHashMap<>(taskClasses.size());
		for (Class<? extends Task> cl : taskClasses) {
			try {
				Task t = cl.newInstance();
				Op op = t.getOp();
				// safety a bit
				if (op.getTaskClassName() == null
						|| "".equals(op.getTaskClassName()))
					op.setTaskClassName(t.getClass().getCanonicalName());
				ops.put(op.getTaskClassName(), op);
				log.info("Successfully registered Op : " + op);
			} catch (InstantiationException | IllegalAccessException e) {
				Utils.logError(log, e, "Unable to register Op for Task: " + cl.getCanonicalName());
			}
		}

		// if initTask then read from file

		// if initWorkflow then read from file

		// if initWorkflowExecution then read from file
	}

	@Transactional(readOnly = true)
	public List<Op> getOps() {
		List<Op> answer = new LinkedList<>(WorkflowService.ops.values());

		return answer;
	}

	public Task createNewTask(Op op) {
		Task answer = null; 

		try {
			@SuppressWarnings("unchecked")
			Class<? extends Task> clazz = (Class<? extends Task>) Class.forName(op.getTaskClassName());

			answer = clazz.newInstance();

		} catch (ClassNotFoundException e) {
			Utils.logError(log, e, "Class not found while creating new task of op : " + op);
		} catch (InstantiationException e) {
			Utils.logError(log, e, "Creating new task of op : " + op + " fails.");
		} catch (IllegalAccessException e) {
			Utils.logError(log, e, "Creating new task of op : " + op + " fails.");
		} 

		return answer;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Task insertTask(Task task) throws Exception {

		if (task.getId() != null
				&& taskRepo.exists(task.getId())) {
			Exception e = Utils.createAndLogError(log, "Task already exists.");
			throw e;
		}

		try {
			task = taskRepo.saveAndFlush(task);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to insert task : " + task);
			e.printStackTrace();
			throw e;
		}

		return task; 
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Task updateTask(Task task) throws Exception {
		if (task.getId() == null
				|| !taskRepo.exists(task.getId())) {
			throw Utils.createAndLogError(log, "Task does not exist.");
		}

		try {
			task = taskRepo.saveAndFlush(task);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to update task : " + task);
		}

		return task;
	}

	@Transactional(readOnly = true) 
	public Task getTask(Task task) {
		if (task.getId() == null)
			return null;
		Task answer = taskRepo.findOne(task.getId());
		return answer;
	}

	@Transactional(readOnly = true)
	public List<Task> getTasks() {
		return taskRepo.findAll();
	}

	public Workflow createNewWorkflow() {
		return new Workflow();
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Workflow insertWorkflow(Workflow workflow) throws Exception{
		if (workflow.getId() != null
				&& workflowRepo.exists(workflow.getId()))
			throw Utils.createAndLogError(log, "Workflow already exists.");

		try {

			for (Task task : workflow.getTasks()) {
				insertTask(task);
			}

			workflowRepo.saveAndFlush(workflow);

			Job j = Utils.buildJob(workflow, jobBuilderFactory, 
					jobRegistryBeanPostProcessor, stepBuilderFactory);

		} catch (Exception e) {
			Utils.logError(log, e, "Unable to insert workflow : " + workflow);
			e.printStackTrace();
			throw e;
		}

		return workflow;
	}

	@Transactional(readOnly = true)
	public Workflow getWorkflow(Workflow workflow) {
		if (workflow.getId() == null)
			return null;

		Workflow answer = workflowRepo.findOne(workflow.getId());
		return answer;
	}

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
		}
		
		return answer;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution queue(Workflow workflow, FileDataset fileDataset) 
			throws JobExecutionException {

		// initializing entity in db
		WorkflowExecution answer = new WorkflowExecution()
				.setWorkflowService(this)
				.setWorkflow(workflow)
				.setFileDataset(fileDataset)
				.setOutput("")
				.setStatus(Status.QUEUED);

		try {
			answer = workflowExecutionRepo.saveAndFlush(answer);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save workflow execution status 'QUEUED'.");
			throw e;
		}

		JobExecution jobExecution = startJobExecution(answer);
		
		answer = answer.setJobExecutionId(jobExecution.getId())
				.setStatus(Status.RUNNING);

		try {
			answer = workflowExecutionRepo.saveAndFlush(answer);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save workflow execution status 'RUNNING'.");
			throw e;
		}

		
		
		return answer;
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution progress(WorkflowExecution workflowExecution, String output) {
		
		StringBuilder outputBuilder = new StringBuilder(workflowExecution.getOutput());
		
		outputBuilder = outputBuilder.append(System.lineSeparator()).append(output);
		
		if (outputBuilder.length() > Constants.MAX_OUTPUT_LENGTH)
			outputBuilder.substring(outputBuilder.length() - Constants.MAX_OUTPUT_LENGTH + 1);
		
		workflowExecution.setOutput(outputBuilder.toString());
		
		try {
			workflowExecution = workflowExecutionRepo.saveAndFlush(workflowExecution);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save workflow execution status 'RUNNING'.");
			throw e;
		}
		
		return workflowExecution;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution finish(WorkflowExecution workflowExecution, boolean success, String output) 
			throws Exception {
		if (output.length() > Constants.MAX_OUTPUT_LENGTH)
			output = output.substring(output.length() - Constants.MAX_OUTPUT_LENGTH + 1);
		workflowExecution = workflowExecutionRepo.findOne(workflowExecution.getId());
		workflowExecution.setOutput(output);
		if (success)
			workflowExecution.setStatus(Status.SUCCESS);
		else
			workflowExecution.setStatus(Status.FAILED);
		try {
			workflowExecution = workflowExecutionRepo.save(workflowExecution);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save new workflow execution status.");
			throw e;
		}
		
		return workflowExecution;
	}
}
