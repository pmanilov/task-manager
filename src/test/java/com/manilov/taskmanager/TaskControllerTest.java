package com.manilov.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manilov.taskmanager.controller.TaskController;
import com.manilov.taskmanager.dto.CommentDto;
import com.manilov.taskmanager.dto.TaskDto;
import com.manilov.taskmanager.model.Priority;
import com.manilov.taskmanager.model.Status;
import com.manilov.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Sample Task");
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.IN_PROGRESS);
        taskDto.setPriority(Priority.HIGH);
        taskDto.setAuthor(5L);
        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sample Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.author").value(5L));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Updated Task");
        taskDto.setDescription("Updated Description");
        taskDto.setStatus(Status.COMPLETED);
        taskDto.setPriority(Priority.LOW);
        taskDto.setAuthor(10L);

        when(taskService.updateTask(eq(1L), any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/task/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.author").value(10L));
    }

    @Test
    void testChangeStatus() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Task");
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.COMPLETED);
        taskDto.setPriority(Priority.HIGH);
        taskDto.setAuthor(5L);

        when(taskService.changeStatus(anyLong(), eq(Status.COMPLETED))).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/task/change-status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"COMPLETED\""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.author").value(5L));
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/task/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Task with id 1 deleted"));
    }


    @Test
    void testGetTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Task");
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.IN_PROGRESS);
        taskDto.setPriority(Priority.HIGH);
        taskDto.setAuthor(5L);

        when(taskService.getTaskById(eq(1L))).thenReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/task/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.author").value(5L));
    }

    @Test
    void testGetExecutableTasks() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Task");
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.IN_PROGRESS);
        taskDto.setPriority(Priority.HIGH);
        taskDto.setAuthor(5L);

        when(taskService.getTasksByExecutor(eq(1L))).thenReturn(Collections.singletonList(taskDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/task/executable/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Task"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].author").value(5L));
    }

    @Test
    void testGetCreatedTasks() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setName("Task");
        taskDto.setDescription("Description");
        taskDto.setStatus(Status.IN_PROGRESS);
        taskDto.setPriority(Priority.HIGH);
        taskDto.setAuthor(5L);

        when(taskService.getTasksByAuthor(eq(1L))).thenReturn(Collections.singletonList(taskDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/task/created/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Task"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].author").value(5L));
    }

    @Test
    void testAddComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment Text");
        commentDto.setUserId(2L);
        commentDto.setTaskId(3L);

        when(taskService.addComment(any(Long.class), any(String.class))).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/task/1/add-comment")
                        .contentType(MediaType.TEXT_PLAIN).content("Comment Text"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.taskId").value(3L))
                .andExpect(jsonPath("$.text").value("Comment Text"));
    }
}

