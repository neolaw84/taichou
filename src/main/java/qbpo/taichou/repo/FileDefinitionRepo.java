package qbpo.taichou.repo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDefinitionRepo extends JpaRepository<FileDefinition, Long> {
	FileDefinition findByName(String name);
	
	FileDefinition findByNameAndFileSchema(String name, FileSchema schema);
}
