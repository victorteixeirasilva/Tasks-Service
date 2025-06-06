package tech.inovasoft.inevolving.ms.tasks.api;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    public static Faker faker = new Faker();

    @Test
    public void addTask_ok() {
        //TODO: Desenvolver teste do End-Point
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
