package qbpo.taichou;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
	public static final int MAX_PATH_LENGTH = 256;

	public static final int MAX_DESCRIPTION_LENGTH = 256;

	public static final int MAX_NOTES_LENGTH = 512;

	public static final int MAX_OUTPUT_LENGTH = 1024;

	public static final int INIT_NUM_OF_FILE_DEFINITIONS_PER_SCHEMA = 2;

	public static final int INIT_NUM_OF_FILE_DATASETS_PER_SCHEMA = 2;

	public static final int INIT_NUM_COLUMNS_OF_FILE_DEFINITIONS_DENSE_EXCEL_CSV = 16;

	////////////////////////////////////////////////////////////////
	
	public static final String MAPPED_BY_FILE_SCHEMA = "fileSchema";

	public static final String FK_FILE_SCHEMA_IN_WORKFLOW = "file_schema_id";

	public static final String MAPPED_BY_FILE_DATASET = "fileDataset";

	public static final String MAPPED_BY_FILE_DEFINITION = "fileDefinition";

	/////////////////////////////////////////////////////////////////
	
	public static final String BATCH_KEY_WORKFLOW_EXECUTION_ID = "workflow-execution-id";

	public static final String BATCH_KEY_FILE_DATASET_PATH = "file-dataset-path";

	public static final String BATCH_KEY_TIME_STAMP = "time-stamp";

	public static final String DEFAULT_FILE_DATASET_DIR = "/media/sf_Shared/workspace/dataset/";

	public static final String BATCH_KEY_STEP_OUTPUT = "step-output";

	@Value("${taichou.op.package:qbpo.taichou.repo}")
	public String OP_PACKAGE = "qbpo.taichou.repo";
	
	@Value("${taichou.primary.package:qbpo.taichou.primary.repo}")
	public String PRIMARY_PACKAGE = "qbpo.taichou.primary.repo";
}
