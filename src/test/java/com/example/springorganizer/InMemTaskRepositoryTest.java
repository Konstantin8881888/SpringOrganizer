package com.example.springorganizer;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemTaskRepositoryTest {

    @Test
    void findAll_ReturnsAllTasks() {
        InMemTaskRepository repository = new InMemTaskRepository();

        List<Task> result = repository.findAll();

        assertNotNull(result);
        assertEquals(4, result.size()); // Предполагается, что 4 задачи в InMemTaskRepository
    }

    @Test
    void save_AddsTaskToList() {
        InMemTaskRepository repository = new InMemTaskRepository();
        Task newTask = new Task("Новая задача");

        repository.save(newTask);

        assertTrue(repository.findAll().contains(newTask));
    }

    @Test
    void findById_TaskExists_ReturnsTask() {
        InMemTaskRepository repository = new InMemTaskRepository();
        UUID existingTaskId = repository.findAll().get(0).id();

        Optional<Task> result = repository.findById(existingTaskId);

        assertTrue(result.isPresent());
        assertEquals(existingTaskId, result.get().id());
    }

    @Test
    void findById_TaskDoesNotExist_ReturnsEmptyOptional() {
        InMemTaskRepository repository = new InMemTaskRepository();
        UUID nonExistingTaskId = UUID.randomUUID();

        Optional<Task> result = repository.findById(nonExistingTaskId);

        assertFalse(result.isPresent());
    }
}