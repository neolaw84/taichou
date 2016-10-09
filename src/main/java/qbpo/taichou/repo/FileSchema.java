package qbpo.taichou.repo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class FileSchema {
	
	public static FileSchema newInstance() {
		FileSchema answer = new FileSchema();
		answer.id = null;
		answer.name = "";
		answer.description = "";
		answer.fileDefinitions = new HashSet<>(Constants.INIT_NUM_OF_FILE_DEFINITIONS_PER_SCHEMA);
		answer.fileDatasets = new HashSet<>(Constants.INIT_NUM_OF_FILE_DATASETS_PER_SCHEMA);
		
		return answer;
	}
	
	@Id
	@GeneratedValue
	Long id;
	
	@Column
	String name;
	
	@Enumerated(EnumType.STRING)
	@OneToMany(mappedBy = Constants.FK_FILE_SCHEMA, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Set<FileDefinition> fileDefinitions;
	
	@Enumerated(EnumType.STRING)
	@OneToMany(mappedBy = Constants.FK_FILE_SCHEMA, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Set<FileDataset> fileDatasets;
	
	@Column(length = Constants.MAX_DESCRIPTION_LENGTH)
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

	public Set<FileDefinition> getFileDefinitions() {
		return fileDefinitions;
	}

	public void setFileDefinitions(Set<FileDefinition> fileDefinitions) {
		this.fileDefinitions = fileDefinitions;
	}

	public Set<FileDataset> getFileDatasets() {
		return fileDatasets;
	}

	public void setFileDatasets(Set<FileDataset> fileDatasets) {
		this.fileDatasets = fileDatasets;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "FileSchema [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
