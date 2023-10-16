package com.example.springorganizer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    InMemTaskRepository inMemTaskRepository;

    @AfterEach
    void tearDown(){
        this.inMemTaskRepository.getTasks().clear();
    }
    @Test
    void handleGetAllTasks_ReturnValidResponseEntity()throws Exception{
        var requestBuilder = get("/api/tasks");
        this.inMemTaskRepository.getTasks().addAll(List.of(new Task(UUID.fromString("71117396-8694-11ed-9ef6-77042ee83937"), "Первая задача", false),
                new Task(UUID.fromString("7172d834-8694-11ed-8669-d7b17d45fba8"), "Вторая задача", false)));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                        "id": "71117396-8694-11ed-9ef6-77042ee83937",
                                        "details": "Первая задача",
                                        "completed": false
                                    },
                                    {
                                        "id": "7172d834-8694-11ed-8669-d7b17d45fba8",
                                        "details": "Вторая задача",
                                        "completed": false
                                    }
                                ]
                                """)
                );
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "details": "Третья задача"
                        }
                        """);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                               {
                                "details": "Третья задача",
                                "completed": false
                                }
                                """),
                        jsonPath("$.id").exists()
                );
        final var tasks = this.inMemTaskRepository.getTasks();
        assertEquals(1, tasks.size());
        assertNotNull(tasks.get(0).id());
        assertEquals("Третья задача", tasks.get(0).details());
        assertFalse(tasks.get(0).completed());

    }
    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "details": null
                        }
                        """);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                               {
                                "errors": ["Описание задачи должно присутствовать и не быть пустым!"]
                                }
                                """, true)
                );
        final var tasks = this.inMemTaskRepository.getTasks();
        assertTrue(tasks.isEmpty());

    }
    @Test
    void handleFindTask_ReturnsTaskIfExists() throws Exception {
        // Подготовка данных
        UUID taskId = UUID.fromString("71117396-8694-11ed-9ef6-77042ee83937");
        Task existingTask = new Task(taskId, "Четвёртая задача", false);
        this.inMemTaskRepository.getTasks().add(existingTask);

        // Выполнение запроса
        var requestBuilder = get("/api/tasks/{id}", taskId);
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                            {
                                "id": "71117396-8694-11ed-9ef6-77042ee83937",
                                "details": "Четвёртая задача",
                                "completed": false
                            }
                            """)
                );
    }

    @Test
    void handleFindTask_InvalidId_ReturnsBadRequest() throws Exception {
        // Запрос на получение задачи по некорректному идентификатору
        mockMvc.perform(get("/api/tasks/{id}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}