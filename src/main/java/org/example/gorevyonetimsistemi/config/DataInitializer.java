package org.example.gorevyonetimsistemi.config;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Role;
import org.example.gorevyonetimsistemi.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;



    @Override
    public void run(String... args) {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }
    }
}
