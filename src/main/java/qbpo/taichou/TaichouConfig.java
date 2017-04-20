package qbpo.taichou;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackageClasses = {TaichouConfig.class})
@Import({
	TaichouOpDataConfig.class, 
	TaichouPrimaryDataConfig.class
})
public class TaichouConfig {
	//TODO include ConfigurationProperties to properly define all taichou prefixed properties
	
	
}
