package org.example.gorevyonetimsistemi.repository;

import org.example.gorevyonetimsistemi.entity.Task;
import org.example.gorevyonetimsistemi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByUser(User user);



}
