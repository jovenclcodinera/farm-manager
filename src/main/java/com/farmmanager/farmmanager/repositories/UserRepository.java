package com.farmmanager.farmmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmmanager.farmmanager.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndEmail(String username, String email);
    User findByEmailAndPassword(String email, String password);
}
