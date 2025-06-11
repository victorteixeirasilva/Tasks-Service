package tech.inovasoft.inevolving.ms.tasks.api;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.inovasoft.inevolving.ms.tasks.api.dto.RequestCreateObjectiveDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    public static Faker faker = new Faker();
    private static final UUID idUser = UUID.randomUUID();

    private UUID addObjective(UUID idUser) {
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(new RequestCreateObjectiveDTO(
                        "Name Objective",
                        "Description Objective",
                        idUser
                ))
                .when()
                .post("http://localhost:8080/ms/objectives")
                .then();

        response.assertThat().statusCode(200).and()
                .body("id", notNullValue());

        return UUID.fromString(response.extract().body().jsonPath().get("id"));
    }

    @Test
    public void addTask_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);

        Assertions.assertNotNull(idTask);

    }

    private UUID addTask(UUID idObjective, UUID idUser) {
        RequestTaskDTO taskDTO = new RequestTaskDTO(
                "Name Task",
                "Description Task",
                LocalDate.now(),
                idObjective,
                idUser
        );

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks";

        ValidatableResponse response = requestSpecification
                .body(taskDTO)
                .when()
                .post(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("id", notNullValue()).and()
                .body("nameTask", equalTo(taskDTO.nameTask())).and()
                .body("descriptionTask", equalTo(taskDTO.descriptionTask())).and()
                .body("status", equalTo(Status.TODO)).and()
                .body("dateTask", equalTo(LocalDate.now().toString())).and()
                .body("idObjective", equalTo(idObjective.toString())).and()
                .body("idUser", equalTo(idUser.toString()));

        return UUID.fromString(response.extract().body().jsonPath().get("id"));
    }

    @Test
    public void repeatTask_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTask_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTasksAndTheirFutureRepetitions_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTaskStatusToDo_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTaskStatusInProgress_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTaskStatusDone_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTaskStatusLate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateTaskStatusCanceled_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void deleteTask_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void deleteTasksAndTheirFutureRepetitions_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void lockTaskByObjective_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksInDateRange_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksInDateRangeByObjectiveId_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksInDate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksLate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksToDoInDateRange_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksToDoInDate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksInProgressInDate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksDoneInDateRange_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksDoneInDate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksCanceledInDateRange_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getTasksCanceledInDate_ok() {
        //TODO: Desenvolver teste do End-Point
    }

}
