package org.example.gorevyonetimsistemi.service;

import org.example.gorevyonetimsistemi.entity.Task;
import org.example.gorevyonetimsistemi.entity.User;

import java.util.List;

public interface TaskService {

    Task createTask(Task task, User user);

    List<Task> getAllTasksForUser(User user);

    Task updateTask(Long taskId, String status, User user);

    void deleteTask(Long taskId, User user);





}
