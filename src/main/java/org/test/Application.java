package org.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.test.config.AppProperties;

@SpringBootApplication(exclude = {
		MultipartAutoConfiguration.class,
		RestTemplateAutoConfiguration.class,
		TaskExecutionAutoConfiguration.class,
		TaskSchedulingAutoConfiguration.class
})
@EnableConfigurationProperties(AppProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
