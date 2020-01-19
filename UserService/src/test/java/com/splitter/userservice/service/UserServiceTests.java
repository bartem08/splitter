package com.splitter.userservice.service;

import com.splitter.userservice.domain.User;
import com.splitter.userservice.exception.RecordConflictException;
import com.splitter.userservice.exception.ResourceNotFoundException;
import com.splitter.userservice.model.UserDto;
import com.splitter.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void findAllUsers_twoUsersExist_shouldReturnListOfUsers() {
        User user1 = User.builder()
                .id(1L)
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("s.artem2549@gmail.com")
                .username("s.artem")
                .firstName("Artem")
                .lastName("Sushko")
                .dateOfBirth(LocalDate.of(1994, 9, 13))
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userService.findAllUsers();

        verify(userRepository, times(1)).findAll();
        assertThat(result.size()).isEqualTo(2);

        UserDto dto1 = result.get(0);
        UserDto dto2 = result.get(1);

        assertEquals(1L, dto1.getId());
        assertEquals("b.artem", dto1.getUsername());
        assertEquals("artem2549@gmail.com", dto1.getEmail());
        assertEquals("Artem", dto1.getFirstName());
        assertEquals("Baranovskyi", dto1.getLastName());
        assertEquals(LocalDate.of(1994, 8, 13), dto1.getDateOfBirth());

        assertEquals(2L, dto2.getId());
        assertEquals("s.artem", dto2.getUsername());
        assertEquals("s.artem2549@gmail.com", dto2.getEmail());
        assertEquals("Artem", dto2.getFirstName());
        assertEquals("Sushko", dto2.getLastName());
        assertEquals(LocalDate.of(1994, 9, 13), dto2.getDateOfBirth());
    }

    @Test
    public void findById_userExists_shouldReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder()
                .id(1L)
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build()));

        UserDto result = userService.findUserById(1L);
        assertEquals(1L, result.getId());
        assertEquals("b.artem", result.getUsername());
        assertEquals("artem2549@gmail.com", result.getEmail());
        assertEquals("Artem", result.getFirstName());
        assertEquals("Baranovskyi", result.getLastName());
        assertEquals(LocalDate.of(1994, 8, 13), result.getDateOfBirth());
    }

    @Test
    public void findById_userDoesNotExist_shouldThrowResourceNotFoundException() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.findUserById(100L));

        assertThat(exception.getMessage()).isEqualTo("User 100 does not exist.");
    }

    @Test
    public void createUser_validDto_shouldReturnDtoOfSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(User.builder()
                .id(1L)
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build());

        UserDto result = userService.createUser(UserDto.builder()
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build());

        assertNotNull(result.getId());
        assertEquals("b.artem", result.getUsername());
        assertEquals("artem2549@gmail.com", result.getEmail());
        assertEquals("Artem", result.getFirstName());
        assertEquals("Baranovskyi", result.getLastName());
        assertEquals(LocalDate.of(1994, 8, 13), result.getDateOfBirth());
    }

    @Test
    public void createUser_emailExists_shouldThrowRecordConflictException() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(RecordConflictException.class, () -> userService
                .createUser(UserDto.builder().email("test").build()));
    }

    @Test
    public void createUser_usernameExists_shouldThrowRecordConflictException() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertThrows(RecordConflictException.class, () -> userService
                .createUser(UserDto.builder().email("test").username("test").build()));
    }
}
