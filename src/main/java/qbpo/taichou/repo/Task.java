package qbpo.taichou.repo;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
	FileSchema fileSchema;
	
	/*@Enumerated(EnumType.STRING)
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	FileDataset fileDataset;*/
	
	/*@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	Map<String, FileDefinition> inputFileDefinitions;
	
	@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	Map<String, FileDefinition> outputFileDefinitions;*/
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@LazyCollection(LazyCollectionOption.FALSE)
	List<FileDefinition> inputFileDefinitions;
	
	@ManyToMany(cascade = CascadeType.MERGE)
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

	/*public FileDataset getFileDataset() {
		return fileDataset;
	}

	public void setFileDataset(FileDataset fileDataset) {
		this.fileDataset = fileDataset;
	}*/

	/*public Map<String, FileDefinition> getInputFileDefinitions() {
		return inputFileDefinitions;
	}

	public void setInputFileDefinitions(Map<String, FileDefinition> fileDefinitions) {
		this.inputFileDefinitions = fileDefinitions;
	}
	
	public Map<String, FileDefinition> getOutputFileDefinitions() {
		return outputFileDefinitions;
	}

	public void setOutputFileDefinitions(Map<String, FileDefinition> outputFileDefinitions) {
		this.outputFileDefinitions = outputFileDefinitions;
	}*/
	
	public abstract Op getOp();
	
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

	//public abstract Map<String, FileDefinition> guessOutputFileDefinitions();
	public abstract List<FileDefinition> guessOutputFileDefinitions();
}
