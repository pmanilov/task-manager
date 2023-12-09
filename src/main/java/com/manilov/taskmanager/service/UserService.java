package com.manilov.taskmanager.service;

import com.manilov.taskmanager.dto.UserDto;
import com.manilov.taskmanager.model.User;
import com.manilov.taskmanager.repository.UserRepository;
import com.manilov.taskmanager.security.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.convertUserToUserDto(userRepository.save(user));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertUserToUserDto).collect(Collectors.toList());
    }

    public User getAuthorizedUser() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(securityUser.getUsername())
                .orElseThrow(() -> new AccessDeniedException("Wrong authorization info"));
    }

    private UserDto convertUserToUserDto(User user) {
        return UserDto.builder().id(user.getId()).email(user.getEmail()).build();
    }
}
