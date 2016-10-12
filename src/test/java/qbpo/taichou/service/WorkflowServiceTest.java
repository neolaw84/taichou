package qbpo.taichou.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import qbpo.taichou.repo.HelloTask;
import qbpo.taichou.repo.Op;
import qbpo.taichou.repo.Task;
import qbpo.taichou.service.FileSchemaService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowServiceTest {

	@Autowired
	WorkflowService workflowService;
	
	@Test
	public void contextLoads() {
		/*Task t = new HelloTask();
		Op op = t.getOp();
		
		Task t2 = workflowService.createNewTask(op);
		
		System.out.println(t2.toString());*/
		
		Task t = new HelloTask();
		Op op = Op.newInstance(t);
		t = workflowService.createNewTask(op);
		t = workflowService.insertTask(t);
		
		workflowService.init();
		
		List<Op> ops = workflowService.getOps();
		
		for (Op o : ops) {
			System.out.println(o.getDescription());
			System.out.println(o.getMoreNotes());
		}
		
	}

}
