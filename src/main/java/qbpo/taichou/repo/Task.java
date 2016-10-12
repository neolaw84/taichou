package qbpo.taichou.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(
		use = JsonTypeInfo.Id.CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = "$taichou.task.class")
public abstract class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	Long id;
	
	@Column
	String name;
	
	@Column
	String description;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	//public abstract Op getOp();
}
