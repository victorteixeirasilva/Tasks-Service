package tech.inovasoft.inevolving.ms.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Date;
import java.util.TimeZone;

@SpringBootApplication
public class TasksApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		SpringApplication.run(TasksApplication.class, args);
	}

}
