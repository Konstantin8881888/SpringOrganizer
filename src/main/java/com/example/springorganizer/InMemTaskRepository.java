package com.example.springorganizer;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemTaskRepository implements TaskRepository {
    private final List<Task> tasks = new LinkedList<>(){{
        this.add(new Task("Первая задача"));
        this.add(new Task("Вторая задача"));
        this.add(new Task("Третья задача"));
        this.add(new Task("Четвёртая задача"));
    }};
    @Override
    public List<Task> findAll() {
        return this.tasks;
    }
}
