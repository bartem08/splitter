package com.splitter.userservice.controller;

import com.splitter.userservice.model.UserDto;
import com.splitter.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserDto> findUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }
}
