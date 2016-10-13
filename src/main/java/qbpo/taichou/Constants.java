package qbpo.taichou;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
	public static final int MAX_PATH_LENGTH = 256;
	
	public static final int MAX_DESCRIPTION_LENGTH = 256;
	
	public static final int MAX_NOTES_LENGTH = 512;
	
	public static final String FK_FILE_SCHEMA = "fileSchema";
	
	public static final String IND_FILE_SCHEMA = "file_schema";
	
	public static final String FK_FILE_DATASET = "fileDataset";
	
	public static final String FK_FILE_DEFINITION = "fileDefinition";
	
	public static final int INIT_NUM_OF_FILE_DEFINITIONS_PER_SCHEMA = 2;
	
	public static final int INIT_NUM_OF_FILE_DATASETS_PER_SCHEMA = 2;
	
	public static final int INIT_NUM_COLUMNS_OF_FILE_DEFINITIONS_DENSE_EXCEL_CSV = 16;
	
	public static final String DEFAULT_FILE_DATASET_DIR = "/media/sf_Shared/workspace/dataset/";
	
	public static final String OP_INIT_VALUE = "${taichou.op.init:true}";
	
	public static String OP_PACKAGE = "qbpo.taichou.repo";
	
	@Value("${taichou.op.package:qbpo.taichou.repo}")
	public String opPackage;
	
	@PostConstruct
	public void init() {
		Constants.OP_PACKAGE = opPackage; 
	}
}
