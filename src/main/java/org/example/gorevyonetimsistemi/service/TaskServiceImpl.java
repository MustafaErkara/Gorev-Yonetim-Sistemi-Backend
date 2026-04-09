package org.example.gorevyonetimsistemi.service;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Task;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.model.TaskStatus;
import org.example.gorevyonetimsistemi.repository.TaskRepository;
import org.example.gorevyonetimsistemi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public Task createTask(Task task, User creator) {
        task.setCreatedBy(creator);
        if(task.getAssignedUser() == null ||task.getAssignedUser().getId() == null){
            throw new RuntimeException("Hata: Görevin atanacağı personel seçilmelidir!");
        }
        User assignedTo = userRepository.findById(task.getAssignedUser().getId())
                .orElseThrow(() -> new RuntimeException("Hata: Atanacak personel sistemde bulunamadı!"));
        task.setAssignedUser(assignedTo);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasksForUser(User user) {
        return taskRepository.findByAssignedUser(user);
    }

    @Override
    public List<Task> getTasksCreatedByMe(User user) {
        return taskRepository.findByCreatedBy(user);
    }

    @Override
    public Task updateTask(Long taskId, String status, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı!"));
        if (!task.getAssignedUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu görevi güncellemeye yetkiniz yoktur.");
        }
        task.setStatus(TaskStatus.valueOf(status));

        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev Bulunamadı"));

        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bu görevi sadece oluşturan yönetici silebilir.");
        }
        taskRepository.delete(task);
    }
}
