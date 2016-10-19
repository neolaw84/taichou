package qbpo.taichou.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.FileDefinition;
import qbpo.taichou.repo.Task;
import qbpo.taichou.repo.FileDefinition.Type;

public class TaskStep implements Tasklet {

	private Log log = LogFactory.getLog(TaskStep.class);
	
	Task task; 

	public Task getTask() {
		return task;
	}

	public TaskStep setTask(Task task) {
		this.task = task;
		return this;
	}

	private String getExtension(FileDefinition fileDefinition) {
		String answer = null;
		if (fileDefinition.getType() == Type.SPARSE_SVM)
			answer = ".svm";
		else if (fileDefinition.getType() == Type.DENSE_VECTOR)
			answer = ".csv";
		else if (fileDefinition.getType() == Type.DENSE_EXCEL_CSV)
			answer = ".csv";

		return answer;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// get the data from step context 
		
		StepContext stepContext = chunkContext.getStepContext();

		if (!stepContext.getJobExecutionContext().containsKey(Constants.FILE_DATASET_PATH)) {
			throw Utils.createAndLogError(log, "File dataset path not found in job execution context.");
		}

		String fileDatasetPath = stepContext.getJobExecutionContext().get(Constants.FILE_DATASET_PATH).toString();

		// arrange data to be passed to Task
		
		List<String> inputFilePaths = new ArrayList<>(task.getInputFileDefinitions().size());

		for (FileDefinition fileDefinition : task.getInputFileDefinitions()) {

			String fileExtension = this.getExtension(fileDefinition);

			String filePath = String.join("", 
					fileDatasetPath, "/", fileDefinition.getName(), fileExtension);

			inputFilePaths.add(filePath);
		}

		List<String> outputFilePaths = new ArrayList<>(task.getOutputFileDefinitions().size());

		for (FileDefinition fileDefinition : task.getOutputFileDefinitions()) {
			String fileExtension = this.getExtension(fileDefinition);

			String filePath = String.join("", 
					fileDatasetPath, "/", fileDefinition.getName(), fileExtension);

			outputFilePaths.add(filePath);
		}
		
		// finish task; get output
		
		String output = task.execute(inputFilePaths, outputFilePaths);

		// put output to step context
		
		stepContext.getStepExecution().getExecutionContext().putString(Constants.STEP_OUTPUT, output);
		
		//stepContext.getStepExecutionContext().put(Constants.STEP_OUTPUT, output);
		
		return null;
	}



}
