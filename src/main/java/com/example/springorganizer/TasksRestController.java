package com.example.springorganizer;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<Task> handleCreateNewTask(@RequestBody NewTaskPayload payload, UriComponentsBuilder uriComponentsBuilder){
        var task = new Task(payload.details());
        this.taskRepository.save(task);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/tasks/{taskId}")
                        .build(Map.of("taskId", task.id())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(task);
    }
}
