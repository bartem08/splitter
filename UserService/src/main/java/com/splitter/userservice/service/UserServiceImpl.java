package com.splitter.userservice.service;

import com.splitter.userservice.domain.User;
import com.splitter.userservice.exception.RecordConflictException;
import com.splitter.userservice.exception.ResourceNotFoundException;
import com.splitter.userservice.model.UserDto;
import com.splitter.userservice.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::daoToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        return userRepository.findById(id)
                .map(this::daoToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " does not exist."));
    }

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RecordConflictException(String.format("%s %s %s", "Email", user.getEmail(), "has already been taken by other user."));
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RecordConflictException(String.format("%s %s %s", "Username", user.getUsername(), "has already been taken by other user."));
        }
        User dao = userRepository.save(dtoToDao(user));
        return daoToDto(dao);
    }

    private UserDto daoToDto(User dao) {
        return UserDto.builder()
                .id(dao.getId())
                .username(dao.getUsername())
                .email(dao.getEmail())
                .firstName(dao.getFirstName())
                .lastName(dao.getLastName())
                .dateOfBirth(dao.getDateOfBirth())
                .build();
    }

    private User dtoToDao(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .build();
    }
}
