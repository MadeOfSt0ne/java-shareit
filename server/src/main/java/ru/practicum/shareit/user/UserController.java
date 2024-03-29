package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addNewUser(@RequestBody UserDto userDto) {
        log.info("SERVER: Add new user {}", userDto);
        return userService.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("SERVER: Update user id = {}, new user = {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("SERVER: Total users = {}", userService.getUsers().size());
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) {
        log.info("SERVER: Get user id = {}", userId);
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("SERVER: Delete user id = {}", userId);
        userService.deleteUser(userId);
    }
}
