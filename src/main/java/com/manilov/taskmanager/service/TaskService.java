package com.manilov.taskmanager.service;

import com.manilov.taskmanager.dto.TaskDto;
import com.manilov.taskmanager.model.Task;
import com.manilov.taskmanager.model.User;
import com.manilov.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;


    public TaskDto createTask(TaskDto taskDto) {
        taskRepository.save(this.convertTaskDtotoTask(taskDto));
        return taskDto;
    }


    public void deleteTask(Long taskId) throws AccessDeniedException, NoSuchElementException {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            Task task = taskOptional.get();
            if(task.getAuthor().getId().equals(userService.getAuthorizedUser().getId())) {
                taskRepository.delete(task);
            } else {
                throw new AccessDeniedException("You don't have permission to delete this task");
            }
        } else {
            throw new NoSuchElementException("Task not found with id: " + taskId);
        }
    }

    public TaskDto updateTask(Long taskId, TaskDto updatedTaskDto) throws AccessDeniedException, NoSuchElementException {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            Task task = taskOptional.get();
            if(task.getAuthor().getId().equals(userService.getAuthorizedUser().getId())) {
                task.setName(updatedTaskDto.getName());
                task.setDescription(updatedTaskDto.getDescription());
                task.setPriority(updatedTaskDto.getPriority());
                task.setStatus(updatedTaskDto.getStatus());
                task.setExecutors(updatedTaskDto.getExecutors().stream()
                        .map(userService::getUserById).collect(Collectors.toSet()));
                taskRepository.save(task);
                return convertTasktoTaskDto(taskRepository.save(task));
            } else {
                throw new AccessDeniedException("You don't have permission to update this task");
            }
        } else {
            throw new NoSuchElementException("Task not found with id: " + taskId);
        }
    }

    public List<Task> getTasksByAuthor(Long authorId) {
        return taskRepository.findAllByAuthorId(authorId);
    }

    public List<Task> getTasksByExecutor(Long executorId) {
        User executor = userService.getUserById(executorId);
        return taskRepository.findAllByExecutors(executor);
    }

    public TaskDto getTaskById(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            Task task = taskOptional.get();
            return convertTasktoTaskDto(task);
        } else {
            throw new NoSuchElementException("Task not found with id: " + taskId);
        }
    }

    private Task convertTaskDtotoTask(TaskDto taskDto) {
        return Task.builder().name(taskDto.getName())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .priority(taskDto.getPriority())
                .author(userService.getUserById(taskDto.getAuthor()))
                .executors(taskDto.getExecutors().stream().map(userService::getUserById)
                        .collect(Collectors.toSet())).build();
    }

    private TaskDto convertTasktoTaskDto(Task task) {
        return TaskDto.builder().id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .author(task.getAuthor().getId())
                .executors(task.getExecutors().stream().map(User::getId)
                        .collect(Collectors.toSet())).build();
    }
}
