package qbpo.taichou.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import qbpo.taichou.Constants;

@Entity
public class FileDataset {
	
	public static FileDataset newInstance(FileSchema fileSchema) {
		FileDataset answer = new FileDataset();
		
		answer.id = null;
		answer.name = "";
		answer.path = Constants.DEFAULT_FILE_DATASET_DIR;
		answer.description = "";
		answer.fileSchema = fileSchema;
		
		//fileSchema.fileDatasets.add(answer);
		
		return answer;
	}

	@Id
	@GeneratedValue
	Long id;
	
	@Column
	String name;
	
	@Column(length = Constants.MAX_PATH_LENGTH)
	String path;
	
	@Column(length = Constants.MAX_DESCRIPTION_LENGTH)
	String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn
	@JsonIgnore
	FileSchema fileSchema;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileDataset other = (FileDataset) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
