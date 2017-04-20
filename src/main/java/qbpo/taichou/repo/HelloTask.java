package qbpo.taichou.repo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.io.IOUtils;

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
	public List<FileDefinition> guessOutputFileDefinitions() { 
		// We know this will output a single column excel csv
		// We also know that there's only one inputFileDefinition
		
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
		
		List<FileDefinition> answer = new ArrayList<>(1);
		
		answer.add(outputFileDefinition);
		
		return answer;
	}

	@Override
	public String execute(List<String> inputFilePaths, List<String> outputFilePaths) {
		String inputFilePath = inputFilePaths.get(0);
		String outputFilePath = outputFilePaths.get(0);
		
		InputStreamReader isr = null;
		BufferedReader br = null;
		OutputStream os = null;
		PrintWriter pw = null;
		
		String hello = "~!@#$%^&*()_+";
		
		if (language.equalsIgnoreCase("english"))
			hello = "Hello";
		else if (language.equalsIgnoreCase("japanese"))
			hello = "Konichiwa";
		
		try {
			isr = new InputStreamReader(new FileInputStream(inputFilePath));
			br = new BufferedReader(isr);
			
			os = new FileOutputStream(outputFilePath);
			pw = new PrintWriter(os);
			
			String line = null;
			while ((line = br.readLine()) != null) {
				pw.println(String.join(" ", hello, line));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(pw);
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
		}
		
		return "Hello Task with " + language + " done.";
	}
}
