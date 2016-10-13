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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.FileDefinition;
import qbpo.taichou.repo.Op;
import qbpo.taichou.repo.Task;
import qbpo.taichou.repo.TaskRepo;
import qbpo.taichou.repo.Workflow;
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
	public List<Task> getTasks() {
		return taskRepo.findAll();
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
}
