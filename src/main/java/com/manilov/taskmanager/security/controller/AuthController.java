package com.manilov.taskmanager.security.controller;

import com.manilov.taskmanager.dto.UserDto;
import com.manilov.taskmanager.model.User;
import com.manilov.taskmanager.security.JwtHelper;
import com.manilov.taskmanager.security.model.JwtRequest;
import com.manilov.taskmanager.security.model.JwtResponse;
import com.manilov.taskmanager.security.service.CustomUserDetailsService;
import com.manilov.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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



    @Operation(summary = "Login to get JWT token", description = "Authenticate user and generate JWT token.")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated and JWT token generated",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponse.class)))
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

    @Operation(summary = "Create a new user", description = "Registers a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "User with this email already exists",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string")))
    })
    @PostMapping("/registration")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid User user){
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User with this email already exists. Please use a different email address.");
    }
}