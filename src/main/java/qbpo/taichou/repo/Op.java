package qbpo.taichou.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import qbpo.taichou.Constants;

/**
 * Op is considered constant throughout runtime.
 * @author neolaw
 *
 */
@Entity
public class Op {
	@Id
	@GeneratedValue
	Long id;
	
	@Column
	String name;
	
	@Column(length = Constants.MAX_DESCRIPTION_LENGTH)
	String description;
	
	@Column
	String taskClassName;
	
	@Column(length = Constants.MAX_NOTES_LENGTH)
	String notes;

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

	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String className) {
		this.taskClassName = className;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
