package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Getting user by id: {}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping()
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto);

        ResponseEntity<Object> result = userClient.createUser(userDto);
        log.info("Создан user: {}", userDto);
//        return userClient.createUser(userDto);
        return result;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Updating user: {}, userId: {}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Deleting user: {}", userId);
        userClient.deleteUser(userId);
    }
}