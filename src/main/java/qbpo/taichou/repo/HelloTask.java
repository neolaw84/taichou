package qbpo.taichou.repo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class HelloTask extends Task{
	@Column
	String nameToSayHello;
}
