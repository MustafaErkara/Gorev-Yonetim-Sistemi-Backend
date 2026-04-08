package org.example.gorevyonetimsistemi.service;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Task;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.model.TaskStatus;
import org.example.gorevyonetimsistemi.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasksForUser(User user) {
        return taskRepository.findByUser(user);
    }

    @Override
    public Task updateTask(Long taskId, String status, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu görevi güncellemeye yetkiniz yoktur.");
        }
        task.setStatus(TaskStatus.valueOf(status));

        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev Bulunamadı"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu görevi silmeye yetkiniz yoktur.");
        }
        taskRepository.delete(task);
    }
}
