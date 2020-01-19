package com.splitter.userservice.service;

import com.splitter.userservice.model.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    UserDto createUser(UserDto user);
}
