package qbpo.taichou.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import qbpo.taichou.Constants;
import qbpo.taichou.service.WorkflowService;

/**
 * This is a copy of jobExecution id. 
 * Id generation is handled by jobExecution in Spring Batch. 
 * @author neolaw
 *
 */
@Entity
public class WorkflowExecution {
	
	@Id
	@GeneratedValue
	Long id;
	
	@Enumerated(EnumType.STRING)
	@ManyToOne(fetch = FetchType.EAGER)
	FileDataset fileDataset;
	
	@Enumerated(EnumType.STRING)
	@ManyToOne(fetch = FetchType.EAGER)
	Workflow workflow;
	
	@Column(length = Constants.MAX_OUTPUT_LENGTH, nullable = true)
	String output;
	
	@Column(nullable = true)
	Long jobExecutionId;
	
	public static enum Status {
		QUEUED, RUNNING, FAILED, SUCCESS
	}
	
	@Column(nullable = true)
	Status status;
	
	@Transient
	WorkflowService workflowService;

	public Long getId() {
		return id;
	}

	public WorkflowExecution setId(Long id) {
		this.id = id;
		return this;
	}

	public FileDataset getFileDataset() {
		return fileDataset;
	}

	public WorkflowExecution setFileDataset(FileDataset fileDataset) {
		this.fileDataset = fileDataset;
		return this;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public WorkflowExecution setWorkflow(Workflow workflow) {
		this.workflow = workflow;
		return this;
	}

	public String getOutput() {
		return output;
	}

	public WorkflowExecution setOutput(String output) {
		this.output = output;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public WorkflowExecution setStatus(Status status) {
		this.status = status;
		return this;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public WorkflowExecution setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
		return this;
	}

	public Long getJobExecutionId() {
		return jobExecutionId;
	}

	public WorkflowExecution setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
		return this;
	}
}
