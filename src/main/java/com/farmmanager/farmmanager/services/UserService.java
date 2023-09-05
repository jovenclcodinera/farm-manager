package com.farmmanager.farmmanager.services;

import com.farmmanager.farmmanager.models.User;
import com.farmmanager.farmmanager.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean checkIfUserExistByUsernameAndEmail(User user) {
        return userRepository.findByUsernameAndEmail(user.getUsername(), user.getEmail()) != null ? true : false;
    }
}
