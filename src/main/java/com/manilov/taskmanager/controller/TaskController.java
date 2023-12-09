package com.manilov.taskmanager.controller;

import com.manilov.taskmanager.dto.TaskDto;
import com.manilov.taskmanager.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        return new ResponseEntity<>(taskService.createTask(taskDto), HttpStatus.CREATED);
    }

    @PostMapping("/update/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) throws AccessDeniedException {
        return new ResponseEntity<>(taskService.updateTask(taskId, taskDto), HttpStatus.OK);
    }

    @PostMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) throws AccessDeniedException {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>("Task with id " + taskId + " deleted", HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long taskId) throws AccessDeniedException {
        return new ResponseEntity<>(taskService.getTaskById(taskId), HttpStatus.OK);
    }
}
