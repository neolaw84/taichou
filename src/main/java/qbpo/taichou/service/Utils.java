package qbpo.taichou.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.Task;
import qbpo.taichou.repo.Workflow;
import qbpo.taichou.repo.WorkflowExecution;

public class Utils {
	static void logError(Log log, Exception e, String message) {
		log.error(message);
		log.error(e.getStackTrace());
	}

	static Exception createAndLogError(Log log, String message) {
		Exception e = new Exception(message);
		logError(log, e, message);
		return e;
	}

	static Job buildJob(Workflow workflow, 
			JobBuilderFactory jobBuilderFactory,
			JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor,
			StepBuilderFactory stepBuilderFactory,
			ExecutionService executionService) {
		List<Task> tasks = workflow.getTasks();
		String jobId = Long.toString(workflow.getId());
		JobBuilder jBuilder = jobBuilderFactory.get(jobId);

		SimpleJobBuilder sjb = null;

		//int engineSequence = 0;
		for (Task task : tasks) {
			Step step = buildStep(task, stepBuilderFactory, executionService); // get a step

			if (sjb == null)
				sjb = jBuilder.start(step);
			else
				sjb.next(step);
		}
		
		sjb.listener(executionService);

		Job j = sjb.build();
		jobRegistryBeanPostProcessor.postProcessAfterInitialization(j, jobId);
		return j;
	}

	static Step buildStep(Task task, StepBuilderFactory stepBuilderFactory, ExecutionService executionService) {
		return stepBuilderFactory.get(Long.toString(task.getId()) + "-" + Long.toString(System.currentTimeMillis()))
				.tasklet(new TaskStep().setTask(task))
				.listener(executionService)
		.build();
	}

	static JobParameters buildJobParameter(WorkflowExecution workflowExecution) {
		JobParametersBuilder jobParameterBuilder = new JobParametersBuilder();

		JobParameters answer = jobParameterBuilder
				.addLong(Constants.TIME_STAMP, System.currentTimeMillis())
				.addLong(Constants.WORKFLOW_EXECUTION_ID, workflowExecution.getId())
				.toJobParameters();
		
		return answer;
	}
	
	static String tail(String data, int maxLength) {
		if (data.length() > maxLength)
			data = data.substring(data.length() - maxLength + 1);
		
		return data;
	}
	
	static String appendAndTail(String data, String toAppend, int maxLength) {
		StringBuilder answerBuilder = new StringBuilder(data);

		answerBuilder = answerBuilder.append(System.lineSeparator()).append(toAppend);

		if (answerBuilder.length() > maxLength)
			answerBuilder.substring(answerBuilder.length() - maxLength + 1);
		
		return answerBuilder.toString();
	}
}
