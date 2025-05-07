package tech.inovasoft.inevolving.ms.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TasksApplication {

	/**
	 * O método main serve como ponto de entrada para a aplicação Spring Boot.
	 * Ele inicia a aplicação invocando o método SpringApplication.run com a
	 * classe TasksApplication e os argumentos da linha de comando.
	 *
	 * @param args argumentos da linha de comando passados para a aplicação
	 */
	public static void main(String[] args) {
		SpringApplication.run(TasksApplication.class, args);
	}

}
