package qbpo.taichou.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import qbpo.taichou.repo.FileDefinition;
import qbpo.taichou.repo.FileSchema;
import qbpo.taichou.service.FileSchemaService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileSchemaServiceTest {

	@Autowired
	FileSchemaService fileSchemaService;
	
	@Test
	public void contextLoads() throws Exception {
		FileSchema schema = fileSchemaService.createNew();
		
		schema.setName("Test Schema");
		schema.setDescription("Test Description");
		
		schema = fileSchemaService.insert(schema);
		
		System.out.println("Schema after insert : " + schema.toString());
		
		FileDefinition def = fileSchemaService.createNewFileDefinition(schema);
		
		def = fileSchemaService.insertFileDefinition(def);
		
		def = fileSchemaService.getFileDefinition(def);
		
		if (def != null)
			schema = def.getFileSchema();
		
		for (FileDefinition d : schema.getFileDefinitions()) {
			System.out.println(d);
		}
		
		System.out.println("Schema after file definition insert : " + schema.toString());
		
		System.out.println("File definition : " + def);
		
		def.setName("New Def Name");
		
		def = fileSchemaService.updateFileDefinition(def);
		
		def = fileSchemaService.getFileDefinition(def);
		
		schema = def.getFileSchema();
		
		for (FileDefinition d : schema.getFileDefinitions())
			System.out.println(d);
		
		FileDefinition def2 = fileSchemaService.createNewFileDefinition(schema);
		
		def2 = fileSchemaService.insertFileDefinition(def2);
		
		def2 = fileSchemaService.getFileDefinition(def2);
		
		schema = def2.getFileSchema();
		
		for (FileDefinition d : schema.getFileDefinitions())
			System.out.println(d);
	}
}
