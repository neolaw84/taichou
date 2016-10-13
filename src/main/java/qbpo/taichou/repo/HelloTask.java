package qbpo.taichou.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class HelloTask extends Task{
	@Column
	String language;
	
	public static Task newInstance() {
		Task answer = new HelloTask();
		
		answer.name = "Hello Task";
		answer.description = "Hello Task description";
		
		return answer;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public Op getOp() {
		
		Op answer = new Op();
		
		answer.taskClassName = HelloTask.class.getCanonicalName();
		answer.name = "Hello Op";
		answer.description = "Hello Op says hello to each of the name in the given file.";
		answer.notes = new HashMap<>(4);
		answer.notes.put("Output", "Hello Op outputs to console.");
		
		return answer;
	}
	
	@Override
	//public Map<String, FileDefinition> guessOutputFileDefinitions() {
	public List<FileDefinition> guessOutputFileDefinitions() { 
		// We know this will output a single column excel csv
		// We also know that there's only one inputFileDefinition
		
		//FileDefinition inputFileDefinition = inputFileDefinitions.get("names");
		FileDefinition inputFileDefinition = inputFileDefinitions.get(0);
		
		List<String> columns = new ArrayList<>(1);
		columns.add("hello_names_in_" + language);
		
		FileDefinition outputFileDefinition = FileDefinition
				.newInstance(inputFileDefinition.getFileSchema())
				.setColumns(columns)
				.setDescription("Hello Name Output in " + language)
				.setFileSchema(inputFileDefinition.getFileSchema())
				.setName("hello_names_" + language + ".csv")
				.setType(FileDefinition.Type.DENSE_EXCEL_CSV);
		
		
		Map<String, FileDefinition> answer = new HashMap<>(1);
		
		answer.put(language + "hello_names", outputFileDefinition);
		
		//return answer;
		return new ArrayList<>(answer.values());
	}
}
