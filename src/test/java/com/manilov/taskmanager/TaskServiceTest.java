package com.manilov.taskmanager;

import com.manilov.taskmanager.dto.CommentDto;
import com.manilov.taskmanager.dto.TaskDto;
import com.manilov.taskmanager.model.*;
import com.manilov.taskmanager.repository.CommentRepository;
import com.manilov.taskmanager.repository.TaskRepository;
import com.manilov.taskmanager.service.TaskService;
import com.manilov.taskmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() throws AccessDeniedException {
        TaskDto taskDto = createTaskDto();
        when(userService.getAuthorizedUser()).thenReturn(createUser(taskDto.getAuthor()));

        assertDoesNotThrow(() -> taskService.createTask(taskDto));

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTask() throws AccessDeniedException {
        Long taskId = 1L;
        Task task = createTask();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.getAuthorizedUser()).thenReturn(createUser(task.getAuthor().getId()));

        assertDoesNotThrow(() -> taskService.deleteTask(taskId));

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testChangeStatus() {
        Long taskId = 1L;
        Status status = Status.IN_PROGRESS;
        Task task = createTask();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);
        when(userService.getAuthorizedUser()).thenReturn(task.getExecutors().iterator().next());

        TaskDto result = assertDoesNotThrow(() -> taskService.changeStatus(taskId, status));

        assertNotNull(result);
        assertEquals(status, result.getStatus());
    }

    @Test
    void testAddComment() {
        Long taskId = 1L;
        String text = "Test comment";
        Task task = createTask();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.getAuthorizedUser()).thenReturn(createUser(1L));

        CommentDto result = assertDoesNotThrow(() -> taskService.addComment(taskId, text));

        assertNotNull(result);
        assertEquals(text, result.getText());
    }

    @Test
    void testGetCommentById() {
        Long commentId = 1L;
        Comment comment = createComment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = assertDoesNotThrow(() -> taskService.getCommentById(commentId));

        assertNotNull(result);
        assertEquals(commentId, result.getId());
    }

    @Test
    void testGetTasksByAuthor() {
        Long authorId = 1L;
        List<Task> tasks = Arrays.asList(createTask(), createTask());
        when(taskRepository.findAllByAuthorId(authorId)).thenReturn(tasks);

        List<TaskDto> result = taskService.getTasksByAuthor(authorId);

        assertNotNull(result);
        assertEquals(tasks.size(), result.size());
    }

    @Test
    void testGetTasksByExecutor() {
        Long executorId = 1L;
        List<Task> tasks = Arrays.asList(createTask(), createTask());
        when(userService.getUserById(executorId)).thenReturn(createUser(executorId));
        when(taskRepository.findAllByExecutors(any(User.class))).thenReturn(tasks);

        List<TaskDto> result = taskService.getTasksByExecutor(executorId);

        assertNotNull(result);
        assertEquals(tasks.size(), result.size());
    }

    @Test
    void testGetTaskById() {
        Long taskId = 1L;
        Task task = createTask();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskDto result = assertDoesNotThrow(() -> taskService.getTaskById(taskId));

        assertNotNull(result);
        assertEquals(taskId, result.getId());
    }

    private TaskDto createTaskDto() {
        return TaskDto.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(Status.PENDING)
                .priority(Priority.HIGH)
                .author(1L)
                .executors(new HashSet<>(Collections.singletonList(1L)))
                .comments(new ArrayList<>())
                .build();
    }

    private Task createTask() {
        return Task.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(Status.PENDING)
                .priority(Priority.HIGH)
                .author(createUser(1L))
                .executors(new HashSet<>(Collections.singletonList(createUser(1L))))
                .comments(new ArrayList<>())
                .build();
    }

    private Comment createComment() {
        return Comment.builder()
                .id(1L)
                .task(createTask())
                .user(createUser(3L))
                .text("Test Comment")
                .build();
    }

    private User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .email("testuser" + userId+"@mail.ru")
                .password("12345678")
                .createdTasks(new ArrayList<>())
                .executableTasks(new ArrayList<>())
                .build();
    }
}

