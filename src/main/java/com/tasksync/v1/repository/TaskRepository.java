package com.tasksync.v1.repository;

import com.tasksync.v1.model.mysql.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);
    List<Task> findByPrioridade(String prioridade);
}
