package qbpo.taichou.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.Task;

public class TaskStep implements Tasklet {

	Task task; 
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		StepContext stepContext = chunkContext.getStepContext();
		
		//String fileDatasetPath = (String) stepContext.getJobParameters().get(Constants.FILE_DATASET_PATH);
		
		String fileDatasetPath = stepContext.getJobExecutionContext().get(Constants.FILE_DATASET_PATH).toString();
		
		System.out.println(fileDatasetPath);
		
		/*if (stepContext.getStepExecution().getJobExecution().getExecutionContext().containsKey(Constants.FILE_DATASET_ID)) 
			fileDataset = stepContext.getStepExecution().getJobExecution().getExecutionContext().getString(Constants.FILE_DATASET_ID);*/
		
		
		return null;
	}

}
