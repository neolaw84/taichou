/**
 * 
 */
package qbpo.taichou.rest;

import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author neolaw
 *
 */
@RestController
@RequestMapping("taichou/schema")
public class SchemaController {
	public List<String> listSchemas() {
		List<String> answer = new LinkedList();
		
		// hard-coding for now
		
		
		
		return answer;
	}
}
