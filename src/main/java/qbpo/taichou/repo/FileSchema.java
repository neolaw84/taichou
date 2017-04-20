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

import qbpo.taichou.Constants;

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
	@OneToMany(mappedBy = Constants.MAPPED_BY_FILE_SCHEMA, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Set<FileDefinition> fileDefinitions;
	
	@Enumerated(EnumType.STRING)
	@OneToMany(mappedBy = Constants.MAPPED_BY_FILE_SCHEMA, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Set<FileDataset> fileDatasets;
	
	@Column(length = Constants.MAX_DESCRIPTION_LENGTH)
	String description;

	public Long getId() {
		return id;
	}

	public FileSchema setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public FileSchema setName(String name) {
		this.name = name;
		return this;
	}

	public Set<FileDefinition> getFileDefinitions() {
		return fileDefinitions;
	}

	public FileSchema setFileDefinitions(Set<FileDefinition> fileDefinitions) {
		this.fileDefinitions = fileDefinitions;
		return this;
	}

	public Set<FileDataset> getFileDatasets() {
		return fileDatasets;
	}

	public FileSchema setFileDatasets(Set<FileDataset> fileDatasets) {
		this.fileDatasets = fileDatasets;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public FileSchema setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String toString() {
		return "FileSchema [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
