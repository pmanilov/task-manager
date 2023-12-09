package com.manilov.taskmanager.controller;

import com.manilov.taskmanager.dto.UserDto;
import com.manilov.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get a list of users", description = "Returns a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
