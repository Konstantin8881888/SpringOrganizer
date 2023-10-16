package com.example.springorganizer;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TasksRestController {
    private final TaskRepository taskRepository;

    public TasksRestController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks(){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.taskRepository.findAll());
    }
}
