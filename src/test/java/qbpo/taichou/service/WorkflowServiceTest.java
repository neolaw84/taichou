package qbpo.taichou.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import qbpo.taichou.repo.FileDefinition;
import qbpo.taichou.repo.FileSchema;
import qbpo.taichou.repo.HelloTask;
import qbpo.taichou.repo.Task;
import qbpo.taichou.repo.Workflow;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowServiceTest {

	private static final Log log = LogFactory.getLog(WorkflowServiceTest.class);
	
	@Autowired
	WorkflowService workflowService;

	@Autowired
	FileSchemaService fileSchemaService;

	@Test
	public void contextLoads() throws Exception {

		FileSchema schema = fileSchemaService.createNewFileSchema();

		schema.setName("HelloFileSchema")
			.setDescription("FileSchema to test HelloWorkflow");

		schema = fileSchemaService.insertFileSchema(schema);

		log.info("Schema inserted.");
		
		log.info("Inserted schema : " + schema.toString());

		FileDefinition fileDefinition = fileSchemaService.createNewFileDefinition(schema);

		fileDefinition = fileSchemaService.insertFileDefinition(fileDefinition);

		fileDefinition = fileSchemaService.getFileDefinition(fileDefinition);

		log.info("File Definition inserted.");
		
		log.info("Inserted file definition : " + fileDefinition.toString());
		
		if (fileDefinition != null)
			schema = fileDefinition.getFileSchema();

		/*for (FileDefinition d : schema.getFileDefinitions()) {
			System.out.println(d);
		}*/

		log.info("Schema after file definition insertion : " + schema.toString());

		fileDefinition.setName("names");

		fileDefinition = fileSchemaService.updateFileDefinition(fileDefinition);

		fileDefinition = fileSchemaService.getFileDefinition(fileDefinition);

		log.info("File definition updated.");
		
		log.info("Updated file definition : " + fileDefinition.toString());
		
		schema = fileDefinition.getFileSchema();

		FileDefinition fileDefinition2 = fileSchemaService.createNewFileDefinition(schema);

		fileDefinition2 = fileSchemaService.insertFileDefinition(fileDefinition2);

		fileDefinition2 = fileSchemaService.getFileDefinition(fileDefinition2);

		schema = fileDefinition2.getFileSchema();

		log.info("Second file definition inserted.");
		
		log.info("File definitions under schema : " + schema.toString() + " are : ");
		
		for (FileDefinition d : schema.getFileDefinitions())
			log.info(d.toString());

		//////////////////////////////////////
		
		log.info("Testing schema done.");

		Task task = workflowService.createNewTask(HelloTask.newInstance().getOp());

		/*Map<String, FileDefinition> fds = new LinkedHashMap<>(4);
		fds.put("names", fileDefinition);
		fds.put("whatever", fileDefinition2);

		task.setInputFileDefinitions(fds);*/
		
		List<FileDefinition> fds = new LinkedList<>();
		fds.add(fileDefinition);
		fds.add(fileDefinition2);

		task.setInputFileDefinitions(fds);
		
		task.getInputFileDefinitions().remove(1);
		
		((HelloTask) task).setLanguage("english");
		
		//Map<String, FileDefinition> outFds = ((HelloTask) task).guessOutputFileDefinitions();
		List<FileDefinition> outFds = ((HelloTask) task).guessOutputFileDefinitions();
		
		//for (FileDefinition fd : outFds.values()) {
		for (FileDefinition fd : outFds) {
			fileSchemaService.insertFileDefinition(fd);
		}
		
		task.setOutputFileDefinitions(outFds);
		task.setFileSchema(schema);
		
		//task = workflowService.insertTask(task);

		log.info("Task initialized.");
		
		Task task2 = workflowService.createNewTask(HelloTask.newInstance().getOp());

		/*Map<String, FileDefinition> fds2 = new LinkedHashMap<>(4);
		fds2.put("names", fileDefinition);
		
		task2.setInputFileDefinitions(fds2);*/
		
		List<FileDefinition> fds2 = new LinkedList<>();
		fds2.add(fileDefinition);
		
		task2.setInputFileDefinitions(fds2);

		((HelloTask) task2).setLanguage("japanese");
		
		//Map<String, FileDefinition> outFds2 = ((HelloTask) task2).guessOutputFileDefinitions();
		List<FileDefinition> outFds2 = ((HelloTask) task2).guessOutputFileDefinitions();
		
		//for (FileDefinition fd : outFds2.values()) {
		for (FileDefinition fd : outFds2) {
			fileSchemaService.insertFileDefinition(fd);
		}
		
		task2.setOutputFileDefinitions(outFds2);
		task2.setFileSchema(schema);
		
		//task2 = workflowService.insertTask(task2);
		
		log.info("Second task initialized.");
		
		Workflow workflow = Workflow.newInstance();

		List<Task> tasks = new ArrayList<>(2);
		tasks.add(task);
		tasks.add(task2);
		
		workflow.setName("HelloWorkflow");
		workflow.setDescription("Workflow to test Hello Task");
		workflow.setFileSchema(schema);
		workflow.setTasks(tasks);
		
		/*List<Task> tasks2 = new ArrayList<>();
		for (Task t : tasks) {
			Task tt = workflowService.insertTask(t);
			tasks2.add(tt);
		}*/
		
		//workflow.setTasks(tasks2);
		
		workflow = workflowService.insertWorkflow(workflow);
		
		workflow = workflowService.getWorkflow(workflow);
		
		log.info("Workflow inserted.");
		
		log.info("Inserted workflow : " + workflow);
		
		log.info("Workflow's Tasks are : ");
		
		for (Task t : workflow.getTasks()) {
			log.info("Task : " + task);
			log.info("Input File Definition : " + t.getInputFileDefinitions());
			log.info("Output File Definition : " + t.getOutputFileDefinitions());
		}
	}

}
