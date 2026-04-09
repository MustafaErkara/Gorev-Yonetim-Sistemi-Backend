package org.example.gorevyonetimsistemi.controller;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.repository.UserRepository;
import org.example.gorevyonetimsistemi.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/employees")
    public List<User> findAll() {
        return userService.findAllEmployees();
    }

}
