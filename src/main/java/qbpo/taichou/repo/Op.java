package qbpo.taichou.repo;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	
	public static Op newInstance(Task task) {
		Op answer = new Op();
		
		answer.name = "";
		answer.description = "";
		answer.notes = "";
		answer.taskClassName = task.getClass().getCanonicalName();
		answer.moreNotes = new HashMap<>(4);
		
		answer.moreNotes.put("test", "test");
		
		return answer;
	}
	
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

	@ElementCollection(fetch = FetchType.EAGER)
	Map<String, String> moreNotes;
	
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

	public Map<String, String> getMoreNotes() {
		return moreNotes;
	}

	public void setMoreNotes(Map<String, String> moreNotes) {
		this.moreNotes = moreNotes;
	}
}
