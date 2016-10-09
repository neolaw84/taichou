package qbpo.taichou.service;

import org.apache.commons.logging.Log;

public class Utils {
	public static void logError(Log log, Exception e, String message) {
		log.error(message);
		log.error(e.getStackTrace());
	}
	
	public static Exception createAndLogError(Log log, String message) {
		Exception e = new Exception(message);
		logError(log, e, message);
		return e;
	}
}
