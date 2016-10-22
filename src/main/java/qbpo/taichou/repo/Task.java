package qbpo.taichou.repo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.batch.core.step.tasklet.Tasklet;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@Enumerated(EnumType.STRING)
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JsonIgnore
	FileSchema fileSchema;
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	protected List<FileDefinition> inputFileDefinitions;
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	List<FileDefinition> outputFileDefinitions;
	
	public final Long getId() {
		return id;
	}

	public final void setId(Long id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}
	
	public FileSchema getFileSchema() {
		return fileSchema;
	}

	public void setFileSchema(FileSchema fileSchema) {
		this.fileSchema = fileSchema;
	}
	
	public List<FileDefinition> getInputFileDefinitions() {
		return inputFileDefinitions;
	}

	public void setInputFileDefinitions(List<FileDefinition> inputFileDefinitions) {
		this.inputFileDefinitions = inputFileDefinitions;
	}

	public List<FileDefinition> getOutputFileDefinitions() {
		return outputFileDefinitions;
	}

	public void setOutputFileDefinitions(List<FileDefinition> outputFileDefinitions) {
		this.outputFileDefinitions = outputFileDefinitions;
	}
	
	public abstract Op getOp();

	public abstract List<FileDefinition> guessOutputFileDefinitions();
	
	public abstract String execute(List<String> inputFilePaths, List<String> outputFilePaths);
}
