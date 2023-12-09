package com.manilov.taskmanager.repository;

import com.manilov.taskmanager.model.Task;
import com.manilov.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAuthorId(Long author_id);

    List<Task> findAllByExecutors(User user);
}
