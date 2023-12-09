package com.manilov.taskmanager.service;

import com.manilov.taskmanager.model.User;
import com.manilov.taskmanager.repository.UserRepository;
import com.manilov.taskmanager.security.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getAuthorizedUser() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(securityUser.getUsername())
                .orElseThrow(() -> new AccessDeniedException("Wrong authorization info"));
    }
}
