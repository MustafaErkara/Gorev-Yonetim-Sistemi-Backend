package org.example.gorevyonetimsistemi.repository;

import org.example.gorevyonetimsistemi.entity.Role;
import org.example.gorevyonetimsistemi.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
