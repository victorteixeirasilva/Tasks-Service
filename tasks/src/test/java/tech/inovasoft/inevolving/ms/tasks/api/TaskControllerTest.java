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
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.*;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;

import java.time.LocalDate;
import java.util.List;
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
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/repeat/"+idUser+"/"+idTask+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(30);

        ValidatableResponse response = requestSpecification
                .body(new DaysOfTheWeekDTO(true,true,true,true,true,true,true))
                .when()
                .post(url)
                .then();

        response.assertThat().statusCode(200);
    }

    @Test
    public void updateTask_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);

        var request = new RequestUpdateTaskDTO(
                "Update Name Task",
                "Update Description Task",
                idObjective
        );

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/"+idUser+"/"+idTask;

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .put(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("id", equalTo(idTask.toString())).and()
                .body("nameTask", equalTo(request.nameTask())).and()
                .body("descriptionTask", equalTo(request.descriptionTask()));

    }

    @Test
    public void updateTasksAndTheirFutureRepetitions_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String urlRepeatTask = "http://localhost:"+port+"/ms/tasks/repeat/"+idUser+"/"+idTask+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(30);

        ValidatableResponse responseRepeatTask = requestSpecification
                .body(new DaysOfTheWeekDTO(true,true,true,true,true,true,true))
                .when()
                .post(urlRepeatTask)
                .then();

        responseRepeatTask.assertThat().statusCode(200);

        var request = new RequestUpdateRepeatTaskDTO();
        request.idObjective = idObjective;
        request.nameTask = "Update Name Task";
        request.descriptionTask = "Update Description Task";
        request.daysOfTheWeekDTO = new DaysOfTheWeekDTO(true,false,true,false,false,false,false);

        String urlUpdateRepeatTask = "http://localhost:"+port+"/ms/tasks/repeat/"+idUser+"/"+idTask+"/"+LocalDate.now().plusDays(30);

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .put(urlUpdateRepeatTask)
                .then();

        response.assertThat().statusCode(200);
    }

    @Test
    public void updateTaskStatusToDo_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/todo/"+idUser+"/"+idTask;

        ValidatableResponse response = requestSpecification
                .when()
                .patch(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("status", equalTo(Status.TODO));
    }

    @Test
    public void updateTaskStatusInProgress_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/progress/"+idUser+"/"+idTask;

        ValidatableResponse response = requestSpecification
                .when()
                .patch(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("status", equalTo(Status.IN_PROGRESS));
    }

    @Test
    public void updateTaskStatusDone_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/done/"+idUser+"/"+idTask;

        ValidatableResponse response = requestSpecification
                .when()
                .patch(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("status", equalTo(Status.DONE));
    }

    @Test
    public void updateTaskStatusLate_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/late/"+idUser+"/"+idTask;

        ValidatableResponse response = requestSpecification
                .when()
                .patch(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("status", equalTo(Status.LATE));
    }

    @Test
    public void updateTaskStatusCanceled_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);

        var request = new RequestCanceledDTO(
                idUser,
                idTask,
                "Cancellation Reason"
        );

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/canceled";

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .patch(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("status", equalTo(Status.CANCELLED)).and()
                .body("cancellationReason", equalTo(request.cancellationReason()));
    }

    @Test
    public void deleteTask_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/"+idUser+"/"+idTask;

        ValidatableResponse response = requestSpecification
                .when()
                .delete(url)
                .then();

        response.assertThat().statusCode(200);
    }

    @Test
    public void deleteTasksAndTheirFutureRepetitions_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String urlRepeatTask = "http://localhost:"+port+"/ms/tasks/repeat/"+idUser+"/"+idTask+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(30);

        ValidatableResponse responseRepeatTask = requestSpecification
                .body(new DaysOfTheWeekDTO(true,true,true,true,true,true,true))
                .when()
                .post(urlRepeatTask)
                .then();

        responseRepeatTask.assertThat().statusCode(200);

        String url = "http://localhost:"+port+"/ms/tasks/repeat/"+idUser+"/"+idTask+"/"+LocalDate.now();

        ValidatableResponse response = requestSpecification
                .when()
                .delete(url)
                .then();

        response.assertThat().statusCode(200).and()
                .body("numberOfDeletedTasks", equalTo(31));
    }

    @Test
    public void lockTaskByObjective_ok() {
        UUID idObjective = addObjective(idUser);

        UUID idTask = addTask(idObjective, idUser);


        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/lock/"+LocalDate.now()+"/"+idUser+"/"+idObjective;

        ValidatableResponse response = requestSpecification
                .when()
                .delete(url)
                .then();

        response.assertThat().statusCode(200);
    }

    @Test
    public void getTasksInDateRange_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective2, idUser);
        addTask(idObjective2, idUser);
        addTask(idObjective2, idUser);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/"+idUser+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(30);

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(6, taskList.size());
    }

    @Test
    public void getTasksInDateRangeByObjectiveId_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective2, idUser);
        addTask(idObjective2, idUser);
        addTask(idObjective2, idUser);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/"+idUser+"/"+idObjective+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(30);

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

    @Test
    public void getTasksInDate_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective2, idUser);
        addTask(idObjective2, idUser);
        addTask(idObjective2, idUser);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/"+idUser+"/"+LocalDate.now();

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(6, taskList.size());
    }

    private void updateStatus(UUID idUser, UUID idTask, String status) {
        UUID idObjective = addObjective(idUser);
        String url = "";

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response;

        switch (status) {
            case Status.TODO:
                url = "http://localhost:"+port+"/ms/tasks/status/todo/"+idUser+"/"+idTask;

                response = requestSpecification
                        .when()
                        .patch(url)
                        .then();

                response.assertThat().statusCode(200).and()
                        .body("status", equalTo(Status.TODO));
                break;
            case Status.IN_PROGRESS:
                url = "http://localhost:"+port+"/ms/tasks/status/progress/"+idUser+"/"+idTask;

                response = requestSpecification
                        .when()
                        .patch(url)
                        .then();

                response.assertThat().statusCode(200).and()
                        .body("status", equalTo(Status.IN_PROGRESS));
                break;
            case Status.LATE:
                url = "http://localhost:"+port+"/ms/tasks/status/late/"+idUser+"/"+idTask;

                response = requestSpecification
                        .when()
                        .patch(url)
                        .then();

                response.assertThat().statusCode(200).and()
                        .body("status", equalTo(Status.LATE));
                break;
            case Status.CANCELLED:
                var request = new RequestCanceledDTO(
                        idUser,
                        idTask,
                        "Cancellation Reason"
                );



                 url = "http://localhost:"+port+"/ms/tasks/status/canceled";

                 response = requestSpecification
                        .body(request)
                        .when()
                        .patch(url)
                        .then();

                response.assertThat().statusCode(200).and()
                        .body("status", equalTo(Status.CANCELLED)).and()
                        .body("cancellationReason", equalTo(request.cancellationReason()));
                break;
            case Status.DONE:
                url = "http://localhost:"+port+"/ms/tasks/status/done/"+idUser+"/"+idTask;

                response = requestSpecification
                        .when()
                        .patch(url)
                        .then();

                response.assertThat().statusCode(200).and()
                        .body("status", equalTo(Status.DONE));
                break;
        }


    }

    @Test
    public void getTasksLate_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        UUID id1 = addTask(idObjective2, idUser);
        UUID id2 = addTask(idObjective2, idUser);
        UUID id3 = addTask(idObjective2, idUser);
        updateStatus(idUser, id1, Status.LATE);
        updateStatus(idUser, id2, Status.LATE);
        updateStatus(idUser, id3, Status.LATE);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/late/"+idUser;

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

    @Test
    public void getTasksToDoInDateRange_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        UUID id1 = addTask(idObjective2, idUser);
        UUID id2 = addTask(idObjective2, idUser);
        UUID id3 = addTask(idObjective2, idUser);
        updateStatus(idUser, id1, Status.LATE);
        updateStatus(idUser, id2, Status.LATE);
        updateStatus(idUser, id3, Status.LATE);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/todo/"+idUser+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(31);

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

    @Test
    public void getTasksToDoInDate_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        UUID id1 = addTask(idObjective2, idUser);
        UUID id2 = addTask(idObjective2, idUser);
        UUID id3 = addTask(idObjective2, idUser);
        updateStatus(idUser, id1, Status.LATE);
        updateStatus(idUser, id2, Status.LATE);
        updateStatus(idUser, id3, Status.LATE);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/todo/"+idUser+"/"+LocalDate.now();

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

    @Test
    public void getTasksInProgressInDate_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        UUID id1 = addTask(idObjective2, idUser);
        UUID id2 = addTask(idObjective2, idUser);
        UUID id3 = addTask(idObjective2, idUser);
        updateStatus(idUser, id1, Status.IN_PROGRESS);
        updateStatus(idUser, id2, Status.IN_PROGRESS);
        updateStatus(idUser, id3, Status.IN_PROGRESS);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/progress/"+idUser+"/"+LocalDate.now();

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

    @Test
    public void getTasksDoneInDateRange_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        UUID id1 = addTask(idObjective2, idUser);
        UUID id2 = addTask(idObjective2, idUser);
        UUID id3 = addTask(idObjective2, idUser);
        updateStatus(idUser, id1, Status.DONE);
        updateStatus(idUser, id2, Status.DONE);
        updateStatus(idUser, id3, Status.DONE);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/done/"+idUser+"/"+LocalDate.now()+"/"+LocalDate.now().plusDays(31);

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

    @Test
    public void getTasksDoneInDate_ok() {
        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);
        UUID idObjective2 = addObjective(idUser);

        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        addTask(idObjective, idUser);
        UUID id1 = addTask(idObjective2, idUser);
        UUID id2 = addTask(idObjective2, idUser);
        UUID id3 = addTask(idObjective2, idUser);
        updateStatus(idUser, id1, Status.DONE);
        updateStatus(idUser, id2, Status.DONE);
        updateStatus(idUser, id3, Status.DONE);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        String url = "http://localhost:"+port+"/ms/tasks/status/done/"+idUser+"/"+LocalDate.now();

        ValidatableResponse response = requestSpecification
                .when()
                .get(url)
                .then();

        response.assertThat().statusCode(200);

        List<Task> taskList = response.extract().body().jsonPath().get();

        Assertions.assertEquals(3, taskList.size());
    }

}
