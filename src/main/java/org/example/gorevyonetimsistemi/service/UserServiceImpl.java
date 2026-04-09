package org.example.gorevyonetimsistemi.service;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

    private final UserRepository userRepository;



    @Override
    public List<User> findAllEmployees() {
        return userRepository.findAll();
    }
}
