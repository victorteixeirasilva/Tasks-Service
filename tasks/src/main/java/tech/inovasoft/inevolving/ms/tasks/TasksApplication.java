package tech.inovasoft.inevolving.ms.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class TasksApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		SpringApplication.run(TasksApplication.class, args);
	}

}
