package qbpo.taichou.service;

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
		Task t = new HelloTask();
		Op op = t.getOp();
		
		Task t2 = workflowService.createNewTask(op);
		
		System.out.println(t2.toString());
	}

}
