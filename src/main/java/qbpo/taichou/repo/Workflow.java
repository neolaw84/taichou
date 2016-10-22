package qbpo.taichou.repo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import qbpo.taichou.Constants;

@Entity
@Table(indexes = {@Index(columnList = "file_schema_id")})
public class Workflow {
	
	@Id
	@GeneratedValue
	Long id;
	
	@Column
	String name;
	
	@Column(length = Constants.MAX_DESCRIPTION_LENGTH)
	String description;
	
	@Enumerated(EnumType.STRING)
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn//(name = Constants.IND_FILE_SCHEMA)
	@JsonIgnore
	FileSchema fileSchema;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	List<Task> tasks;

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

	public FileSchema getFileSchema() {
		return fileSchema;
	}

	public void setFileSchema(FileSchema fileSchema) {
		this.fileSchema = fileSchema;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public String toString() {
		return "Workflow [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
