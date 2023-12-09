package com.manilov.taskmanager.security.controller;

import com.manilov.taskmanager.model.User;
import com.manilov.taskmanager.security.JwtHelper;
import com.manilov.taskmanager.security.model.JwtRequest;
import com.manilov.taskmanager.security.model.JwtResponse;
import com.manilov.taskmanager.security.service.CustomUserDetailsService;
import com.manilov.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtHelper helper;
    private final UserService userService;



    //get JWT token
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        this.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .email(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e){
            throw new BadCredentialsException(" Invalid email or password !!");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler(){
        return "Credentials Invalid !!";
    }

    @PostMapping("/registration")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user){
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User with this email already exists. Please use a different email address.");
    }
}