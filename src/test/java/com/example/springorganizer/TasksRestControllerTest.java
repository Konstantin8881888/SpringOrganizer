package com.example.springorganizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest {
    @Mock
    TaskRepository taskRepository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    TasksRestController controller;
    @Test
    void handleGetAllTasks_ReturnValidResponseEntity() {
        var tasks = List.of(new Task(UUID.randomUUID(), "Первая задача", false),
                new Task(UUID.randomUUID(), "Вторая задача", false));
        doReturn(tasks).when(this.taskRepository).findAll();

        var responseEntity = this.controller.handleGetAllTasks();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

@Test
void handleFindTask_TaskExists_ReturnsValidResponseEntity() {
    // Создаем тестовые данные
    UUID taskId = UUID.randomUUID();
    Task task = new Task(taskId, "2 задача", false);

    // Мокируем поведение taskRepository
    doReturn(Optional.of(task)).when(taskRepository).findById(taskId);

    // Вызываем тестируемый метод
    Optional<Task> result = controller.handleFindTask(taskId);

    // Проверяем результат
    // тесты на HttpStatus и MediaType не имеют смысла, потому что эти аспекты управления HTTP ответом обрабатываются в контексте контроллера, когда создается ResponseEntity внутри метода.
    assertTrue(result.isPresent());
    assertEquals(task, result.get());

    // Проверяем, что взаимодействовали только с методом findById
    verify(taskRepository).findById(taskId);
    verifyNoMoreInteractions(taskRepository);
}

    @Test
    void handleFindTask_TaskNotFound_ReturnsNotFoundResponseEntity() {
        // Создаем тестовые данные
        UUID id = UUID.randomUUID();

        // Мокируем поведение taskRepository
        doReturn(Optional.empty()).when(taskRepository).findById(id);

        // Вызываем тестируемый метод
        Optional<Task> result = controller.handleFindTask(id);

        // Проверяем результат
        // тесты на HttpStatus и MediaType не имеют смысла, см. выше
        assertNotNull(result);
        assertFalse(result.isPresent());

        // Проверяем, что взаимодействовали только с методом findById
        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() {
        var details = "1 задача";

        var responseEntity = this.controller.handleCreateNewTask(new NewTaskPayload(details), UriComponentsBuilder.fromUriString("http://localhost:8080"), Locale.ENGLISH);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        if (responseEntity.getBody() instanceof Task task){
            assertNotNull(task.id());
            assertEquals(details,task.details());
            assertFalse(task.completed());
            assertEquals(URI.create("http://localhost:8080/api/tasks/" + task.id()), responseEntity.getHeaders().getLocation());
            verify(this.taskRepository).save(task);
        }
        else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }
        verifyNoMoreInteractions(this.taskRepository);
    }
    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() {
        var details = "  ";
        var locale = Locale.US;
        var errorMessage = "Details is empty";
        doReturn(errorMessage).when(this.messageSource).getMessage("task.not.allowed", new Object[0], locale);

        var responseEntity = this.controller.handleCreateNewTask(new NewTaskPayload(details), UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of(errorMessage)), responseEntity.getBody());
        verifyNoInteractions(taskRepository);
    }
}