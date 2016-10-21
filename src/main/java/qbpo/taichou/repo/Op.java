package qbpo.taichou.repo;

import java.util.Map;

/**
 * An Op is basically a Task template.
 * Op is considered constant throughout runtime.
 * Therefore, it is not to be init through a json file.
 * It also is not persisted in the database.
 * @author neolaw
 *
 */
public class Op {
	
	String name;
	
	String description;
	
	String taskClassName;
	
	Map<String, String> notes;
	
	public String getName() {
		return name;
	}

	public Op setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Op setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getTaskClassName() {
		return taskClassName;
	}

	public Op setTaskClassName(String className) {
		this.taskClassName = className;
		return this;
	}

	public Map<String, String> getNotes() {
		return notes;
	}

	public Op setNotes(Map<String, String> notes) {
		this.notes = notes;
		return this;
	}
}
