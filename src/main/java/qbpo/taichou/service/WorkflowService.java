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
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
@EnableBatchProcessing
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

	//////////////////////////////////////////////////////////

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobRegistry jobRegistry;
	
	@Autowired
	private ExecutionService executionService;

	//@Autowired
	//@Qualifier("customJobRegistryBeanPostProcessor")
	//private JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor;

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor bpp = new JobRegistryBeanPostProcessor();
		bpp.setJobRegistry(jobRegistry);
		return bpp;
	}

	//////////////////////////////////////////////////////////////

	@Autowired
	Constants constants;
	
	@PostConstruct
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void init() {

		Reflections reflections = new Reflections(constants.OP_PACKAGE);
		log.debug(constants.OP_PACKAGE);
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
					jobRegistryBeanPostProcessor(), stepBuilderFactory,
					executionService);

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

	///////////////////////////////////////////////////////////

	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution nullToQueue(Workflow workflow, FileDataset fileDataset) 
			throws JobExecutionException {

		// initializing entity in db -- from id == null to Status.QUEUED
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

		log.info("null to Q");
		
		return answer;
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution queueToRunning(Long workflowExecutionId, Long JobExecutionId) 
			throws JobExecutionException {

		// initializing entity in db -- from Status.QUEUED to Status.RUNNING
		WorkflowExecution answer = getWorkflowExecution(workflowExecutionId);

		if (answer.getStatus() != WorkflowExecution.Status.QUEUED)
			return null;
		
		answer.setStatus(Status.RUNNING)
			.setJobExecutionId(JobExecutionId);
		
		try {
			answer = workflowExecutionRepo.saveAndFlush(answer);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save workflow execution status 'RUNNING'.");
			throw e;
		}

		log.info("q to r");
		
		return answer;
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution runningToDone(Long workflowExecutionId, boolean success) 
			throws Exception {
		WorkflowExecution answer = workflowExecutionRepo.findOne(workflowExecutionId);
		
		if (answer.getStatus() != WorkflowExecution.Status.RUNNING)
			return null;
		
		if (success)
			answer.setStatus(Status.SUCCESS);
		else
			answer.setStatus(Status.FAILED);
		try {
			answer = workflowExecutionRepo.save(answer);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save new workflow execution status.");
			throw e;
		}

		log.info("r to end");
		
		return answer;
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
	public WorkflowExecution progress(Long workflowExecutionId, String output) {

		WorkflowExecution answer = workflowExecutionRepo.findOne(workflowExecutionId);
		
		String newOutput = Utils.appendAndTail(answer.getOutput(), output, Constants.MAX_OUTPUT_LENGTH);
		
		answer.setOutput(newOutput);

		try {
			answer = workflowExecutionRepo.saveAndFlush(answer);
		} catch (Exception e) {
			Utils.logError(log, e, "Unable to save additional workflow outputs.");
			throw e;
		}

		return answer;
	}
	
	@Transactional(readOnly = true)
	WorkflowExecution getWorkflowExecution(Long workflowExecutionId) {
		if (workflowExecutionId == null
				|| !workflowExecutionRepo.exists(workflowExecutionId))
			return null;
		
		return workflowExecutionRepo.findOne(workflowExecutionId);
	}
	
	@Transactional(readOnly = true)
	String getWorkflowExecutionFileDatasetPath(Long workflowExecutionId) {
		WorkflowExecution workflowExecution = getWorkflowExecution(workflowExecutionId);
		if (workflowExecution == null
				|| workflowExecution.getFileDataset() == null)
			return null;
		
		return workflowExecution.getFileDataset().getPath();
	}
	
	@Transactional(readOnly = true)
	public WorkflowExecution getWorkflowExecution(WorkflowExecution workflowExecution) {
		if (workflowExecution.getId() == null
				|| !workflowExecutionRepo.exists(workflowExecution.getId()))
			return null;
		
		return workflowExecutionRepo.findOne(workflowExecution.getId());
	}
	
}
