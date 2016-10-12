package qbpo.taichou.repo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class HelloTask extends Task{
	@Column
	String nameToSayHello;

	@Override
	public Op getOp() {
		Op answer = new Op();
		
		answer.name = "HelloTask";
		answer.description = "Hello World task of Taichou";
		answer.taskClassName = getClass().getCanonicalName();
		answer.notes = "";
		
		return answer;
	}
}
