package qbpo.taichou.service;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qbpo.taichou.Constants;
import qbpo.taichou.repo.Op;
import qbpo.taichou.repo.OpRepo;
import qbpo.taichou.repo.Task;

@Service
@Transactional
public class WorkflowService {
	
	private static final Log log = LogFactory.getLog(WorkflowService.class);
	
	@Autowired
	OpRepo opRepo;
	
	@Value(Constants.OP_INIT_VALUE)
	public boolean opInit;
	
	// later, include op.init.by.json.file
	
	@PostConstruct
	@Transactional
	public void init() {
		if (opInit) {
			Reflections reflections = new Reflections(Constants.OP_PACKAGE);
			Set<Class<? extends Task>> taskClasses = reflections.getSubTypesOf(Task.class);
			for (Class<? extends Task> cl : taskClasses) {
				try {
					Task t = cl.newInstance();
					Op op = t.getOp();
					if (op.getTaskClassName() == null
							|| "".equals(op.getTaskClassName()))
						op.setTaskClassName(t.getClass().getCanonicalName());
					op = opRepo.saveAndFlush(op);
					log.info("Successfully registered Op : " + op);
				} catch (InstantiationException | IllegalAccessException e) {
					Utils.logError(log, e, "Unable to register Op for Task: " + cl.getCanonicalName());
				}
			}
		}
	}
	
	@Transactional(readOnly = true)
	public List<Op> getOps() {
		List<Op> answer = opRepo.findAll();
		
		return answer;
	}
	
	public Task createNewTask(Op op) {
		Task answer = null; 
		
		try {
			Class<? extends Task> clazz = (Class<? extends Task>) Class.forName(op.getTaskClassName());
			
			answer = clazz.newInstance();
			
			answer.setName(op.getName() + " Task");
			answer.setDescription(op.getDescription());
			
		} catch (ClassNotFoundException e) {
			Utils.logError(log, e, "Class not found while creating new task of op : " + op);
		} catch (InstantiationException e) {
			Utils.logError(log, e, "Creating new task of op : " + op + " fails.");
		} catch (IllegalAccessException e) {
			Utils.logError(log, e, "Creating new task of op : " + op + " fails.");
		} 
		
		return answer;
	}
}
