package qbpo.taichou;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = {TaichouConfig.class})
@Import({
	TaichouOpDataConfig.class, 
	TaichouPrimaryDataConfig.class
})
public class TaichouConfig {
	//TODO include ConfigurationProperties to properly define all taichou prefixed properties
}
