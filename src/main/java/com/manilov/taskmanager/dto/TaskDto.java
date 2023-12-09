package com.manilov.taskmanager.dto;

import com.manilov.taskmanager.model.Priority;
import com.manilov.taskmanager.model.Status;
import com.manilov.taskmanager.model.User;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private Status status;
    private Priority priority;
    private Long author;
    private Set<Long> executors;
}
