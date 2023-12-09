package com.manilov.taskmanager.controller;

import com.manilov.taskmanager.dto.TaskDto;
import com.manilov.taskmanager.service.TaskService;
import com.manilov.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Operation(summary = "Create a new task", description = "Creates a new task and returns the created task.")
    @ApiResponse(responseCode = "201", description = "Task created successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @PostMapping("/create")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) throws AccessDeniedException {
        return new ResponseEntity<>(taskService.createTask(taskDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing task", description = "Updates an existing task and returns the updated task.")
    @ApiResponse(responseCode = "200", description = "Task updated successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @PostMapping("/update/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) throws AccessDeniedException {
        return new ResponseEntity<>(taskService.updateTask(taskId, taskDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete a task by ID", description = "Deletes a task by ID.")
    @ApiResponse(responseCode = "200", description = "Task deleted successfully")
    @PostMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) throws AccessDeniedException {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>("Task with id " + taskId + " deleted", HttpStatus.OK);
    }

    @Operation(summary = "Get a task by ID", description = "Gets a task by ID.")
    @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long taskId) throws AccessDeniedException {
        return new ResponseEntity<>(taskService.getTaskById(taskId), HttpStatus.OK);
    }

    @Operation(summary = "Get executable tasks", description = "Gets tasks that can be executed.")
    @ApiResponse(responseCode = "200", description = "Executable tasks retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @GetMapping("/executable")
    public ResponseEntity<List<TaskDto>> getExecutableTasks() {
        return new ResponseEntity<>(taskService.getTasksByExecutor(userService.getAuthorizedUser().getId()), HttpStatus.OK);
    }

    @Operation(summary = "Get tasks created by the authenticated user", description = "Gets tasks created by the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Created tasks retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @GetMapping("/created")
    public ResponseEntity<List<TaskDto>> getCreatedTasks() {
        return new ResponseEntity<>(taskService.getTasksByAuthor(userService.getAuthorizedUser().getId()), HttpStatus.OK);
    }
}
