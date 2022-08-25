package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addNewUser(@RequestBody @Valid UserDto userDto) {
        log.info("GATEWAY: Add new user {}", userDto);
        return userClient.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserDto userDto, @PathVariable long userId) {
        log.info("GATEWAY: Update user id = {}, new user = {}", userId, userDto);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GATEWAY: Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("GATEWAY: Get user id = {}", userId);
        return userClient.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("GATEWAY: Delete user id = {}", userId);
        userClient.deleteUser(userId);
    }
}
