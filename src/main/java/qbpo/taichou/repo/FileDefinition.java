package qbpo.taichou.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

@Entity
public class FileDefinition {
	
	public static FileDefinition newInstance() {
		FileDefinition answer = new FileDefinition();
		
		answer.id = null;
		answer.name = "";
		answer.description = "";
		answer.fileSchema = null;
		answer.type = Type.DENSE_EXCEL_CSV;
		answer.columns = new ArrayList<>(Constants.INIT_NUM_COLUMNS_OF_FILE_DEFINITIONS_DENSE_EXCEL_CSV);
		
		return answer;
	}
	
	public static FileDefinition newInstance(FileSchema fileSchema) {
		FileDefinition answer = new FileDefinition();
		
		answer.id = null;
		answer.name = "";
		answer.description = "";
		answer.fileSchema = fileSchema;
		answer.type = Type.DENSE_EXCEL_CSV;
		answer.columns = new ArrayList<>(Constants.INIT_NUM_COLUMNS_OF_FILE_DEFINITIONS_DENSE_EXCEL_CSV);
		
		//fileSchema.fileDefinitions.add(answer);
		
		return answer;
	}
	
	@Id
	@GeneratedValue
	Long id;
	
	@Column
	String name;
	
	@Column(length = Constants.MAX_DESCRIPTION_LENGTH)
	String description;
	
	@ManyToOne
	@JoinColumn
	FileSchema fileSchema;
	
	public static enum Type {
		DENSE_EXCEL_CSV, DENSE_VECTOR, SPARSE_SVM
	}
	
	@Column
	Type type;
	
	@OrderColumn
	@ElementCollection(fetch = FetchType.EAGER)
	List<String> columns;

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
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
		FileDefinition other = (FileDefinition) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileDefinition [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
