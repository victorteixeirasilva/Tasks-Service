package tech.inovasoft.inevolving.ms.tasks.unit.success;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tech.inovasoft.inevolving.ms.tasks.controller.RestExceptionHandler;
import tech.inovasoft.inevolving.ms.tasks.controller.TaskController;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.request.RequestTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.dto.response.ResponseTaskDTO;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Status;
import tech.inovasoft.inevolving.ms.tasks.domain.model.Task;
import tech.inovasoft.inevolving.ms.tasks.domain.util.UserTimezoneResolver;
import tech.inovasoft.inevolving.ms.tasks.service.RecurringTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.SimpleTaskService;
import tech.inovasoft.inevolving.ms.tasks.service.TaskService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TaskController.class, RestExceptionHandler.class})
class TaskTimestampsControllerWebMvcTest {

    private static final String VALID_TOKEN = "valid-token";
    private static final ZoneId USER_ZONE = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SimpleTaskService simpleTaskService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private RecurringTaskService recurringTaskService;

    @MockBean
    private TokenService tokenService;

    @Test
    void addTask_withValidTimezone_returns200AndTimestampFieldsInJson() throws Exception {
        // Given
        UUID idTask = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        UUID idObjective = UUID.randomUUID();
        Instant created = Instant.parse("2026-05-29T15:00:00Z");

        Task task = new Task(
                idTask, "Tarefa", "Desc", Status.TODO, Date.valueOf("2026-05-29"),
                idObjective, idUser, null, null, false, false, false, null, null,
                created, null, null, null
        );
        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa", "Desc", LocalDate.of(2026, 5, 29), idObjective, idUser
        );

        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));
        when(simpleTaskService.addTask(any(), eq(USER_ZONE)))
                .thenReturn(new ResponseTaskDTO(task, USER_ZONE));

        // When
        MvcResult asyncResult = mockMvc.perform(post("/ms/tasks/{token}", VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(UserTimezoneResolver.HEADER_NAME, "America/Sao_Paulo")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Then
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.inProgressAt").value(nullValue()))
                .andExpect(jsonPath("$.completedAt").value(nullValue()))
                .andExpect(jsonPath("$.cancelledAt").value(nullValue()))
                .andExpect(jsonPath("$.id").value(idTask.toString()));
    }

    @Test
    void addTask_withInvalidTimezone_returns400() throws Exception {
        // Given
        UUID idUser = UUID.randomUUID();
        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa", "Desc", LocalDate.of(2026, 5, 29), UUID.randomUUID(), idUser
        );
        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));

        // When / Then — exception thrown before async completion
        mockMvc.perform(post("/ms/tasks/{token}", VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(UserTimezoneResolver.HEADER_NAME, "Not/A/Zone")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.simpleName").value("InvalidTimezoneException"))
                .andExpect(jsonPath("$.message").value("Invalid timezone: Not/A/Zone"));
    }

    @Test
    void addTask_withInvalidToken_returns401() throws Exception {
        // Given
        RequestTaskDTO requestDto = new RequestTaskDTO(
                "Tarefa", "Desc", LocalDate.of(2026, 5, 29), UUID.randomUUID(), UUID.randomUUID()
        );
        when(tokenService.validateToken("bad-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        // When
        MvcResult asyncResult = mockMvc.perform(post("/ms/tasks/{token}", "bad-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(UserTimezoneResolver.HEADER_NAME, "America/Sao_Paulo")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Then
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTask_withValidTimezone_returnsTaskViewWithTimestampFields() throws Exception {
        // Given
        UUID idUser = UUID.randomUUID();
        UUID idTask = UUID.randomUUID();
        Instant created = Instant.parse("2026-05-29T12:00:00Z");
        Task task = new Task(
                idTask, "Task", "Desc", Status.TODO, Date.valueOf("2026-05-29"),
                null, idUser, null, null, false, false, false, null, null,
                created, null, null, null
        );

        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("consumer", "tasks"));
        when(taskService.getTask(idUser, idTask)).thenReturn(task);

        // When
        MvcResult asyncResult = mockMvc.perform(get("/ms/tasks/task/{idUser}/{idTask}/{token}", idUser, idTask, VALID_TOKEN)
                        .header(UserTimezoneResolver.HEADER_NAME, "America/Sao_Paulo"))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Then
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.inProgressAt").value(nullValue()))
                .andExpect(jsonPath("$.completedAt").value(nullValue()))
                .andExpect(jsonPath("$.cancelledAt").value(nullValue()))
                .andExpect(jsonPath("$.id").value(idTask.toString()));
    }
}
