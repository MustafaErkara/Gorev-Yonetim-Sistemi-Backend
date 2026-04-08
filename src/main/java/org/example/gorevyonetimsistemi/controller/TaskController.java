package org.example.gorevyonetimsistemi.controller;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Task;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.service.TaskService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping()
    public Task createTask(@RequestBody Task task,
                           @RequestAttribute("authenticatedUser") User user) {
        return taskService.createTask(task, user);
    }

    @GetMapping()
    public List<Task> getMyTasks(@RequestAttribute("authenticatedUser") User user) {
        if (user == null) {
            throw new RuntimeException("Yetkisiz erişim: Kullanıcı bilgisi alınamadı!");
        }
        return taskService.getAllTasksForUser(user);
    }

    @PutMapping("{id}/status")
    public Task updateStatus(@PathVariable Long id,
                             @RequestParam String status,
                             @RequestAttribute("authentiacationUser" ) User user) {
        return taskService.updateTask(id, status, user);
    }

    public String deleteTask(@PathVariable Long id,
                             @RequestAttribute("authemticationUser") User user) {
        taskService.deleteTask(id, user);
        return "Görev başarıyla silinmiştir.";
    }









}
