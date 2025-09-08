package com.mateja.f1betting.adapter.web.rest.controller;

import com.mateja.f1betting.adapter.web.rest.dto.user.UserDto;
import com.mateja.f1betting.domain.usecase.UserManagementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management operations")
public class UserController {
    private final UserManagementUseCase userManagementUseCase;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details", description = "Retrieve user information including current balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<ResponseEntity<UserDto>> getUser(@PathVariable final String userId) {
        return userManagementUseCase.getUserById(userId)
                .map(user -> new UserDto(user.getUserId(), user.getBalance()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register new user",
            description = "Register a new user with default balance of 100 EUR")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public Mono<ResponseEntity<UserDto>> registerUser(@PathVariable final String userId) {
        return userManagementUseCase.registerUserIfNotExists(userId)
                .map(user -> new UserDto(user.getUserId(), user.getBalance()))
                .map(ResponseEntity::ok);
    }
}