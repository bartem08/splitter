package com.splitter.userservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splitter.userservice.exception.RecordConflictException;
import com.splitter.userservice.exception.ResourceNotFoundException;
import com.splitter.userservice.model.ErrorResponse;
import com.splitter.userservice.model.UserDto;
import com.splitter.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void findUsers_userExists_shouldValidResponseWithBody() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .email("s.artem2549@gmail.com")
                .username("s.artem")
                .firstName("Artem")
                .lastName("Sushko")
                .dateOfBirth(LocalDate.of(1994, 9, 13))
                .build();
        when(userService.findAllUsers()).thenReturn(Arrays.asList(userDto1, userDto2));

        String response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<UserDto> result = objectMapper.readValue(response, new TypeReference<List<UserDto>>() { });
        assertThat(result.size()).isEqualTo(2);

        UserDto user1 = result.get(0);
        UserDto user2 = result.get(1);

        assertEquals(1L, user1.getId());
        assertEquals("b.artem", user1.getUsername());
        assertEquals("artem2549@gmail.com", user1.getEmail());
        assertEquals("Artem", user1.getFirstName());
        assertEquals("Baranovskyi", user1.getLastName());
        assertEquals(LocalDate.of(1994, 8, 13), user1.getDateOfBirth());

        assertEquals(2L, user2.getId());
        assertEquals("s.artem", user2.getUsername());
        assertEquals("s.artem2549@gmail.com", user2.getEmail());
        assertEquals("Artem", user2.getFirstName());
        assertEquals("Sushko", user2.getLastName());
        assertEquals(LocalDate.of(1994, 9, 13), user2.getDateOfBirth());
    }

    @Test
    public void findUserById_userExists_shouldReturnResponseWithBody() throws Exception {
        when(userService.findUserById(1L)).thenReturn(UserDto.builder()
                .id(1L)
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build());

        String response = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto result = objectMapper.readValue(response, UserDto.class);
        assertEquals(1L, result.getId());
        assertEquals("b.artem", result.getUsername());
        assertEquals("artem2549@gmail.com", result.getEmail());
        assertEquals("Artem", result.getFirstName());
        assertEquals("Baranovskyi", result.getLastName());
        assertEquals(LocalDate.of(1994, 8, 13), result.getDateOfBirth());
    }

    @Test
    public void findUserById_userDoesNotExists_shouldReturnErrorResponseWithErrorMessage() throws Exception {
        when(userService.findUserById(100L)).thenThrow(new ResourceNotFoundException("User 100 does not exist."));

        String response = mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ErrorResponse result = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(result.getMessage()).isEqualTo("User 100 does not exist.");
        assertThat(result.getTimestamp()).isNotNull();
    }

    @Test
    public void createUser_validRequestBody_shouldReturnCreatedUserResponse() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("artem2549@gmail.com")
                .username("b.artem")
                .firstName("Artem")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 13))
                .build();
        String requestBody = objectMapper.writeValueAsString(userDto);

        userDto.setId(1L);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto result = objectMapper.readValue(response, UserDto.class);
        assertNotNull(result.getId());
        assertEquals("b.artem", result.getUsername());
        assertEquals("artem2549@gmail.com", result.getEmail());
        assertEquals("Artem", result.getFirstName());
        assertEquals("Baranovskyi", result.getLastName());
        assertEquals(LocalDate.of(1994, 8, 13), result.getDateOfBirth());
    }

    @Test
    public void createUser_InvalidEmail_shouldReturnErrorResponse() throws Exception {
        String requestBody = objectMapper.writeValueAsString(UserDto.builder()
                .email("alex2549gmail.com")
                .username("b.alex")
                .firstName("Alex")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 14))
                .build());
        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Email alex2549gmail.com is invalid.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    public void createUser_userDateOfBirthInTheFuture_shouldReturnErrorResponse() throws Exception {
        String requestBody = objectMapper.writeValueAsString(UserDto.builder()
                .email("alex2549@gmail.com")
                .username("b.alex")
                .firstName("Alex")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(9999, 8, 14))
                .build());
        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Date of birth 9999-08-14 is invalid.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
    
    @Test
    public void createUser_userEmailIsNotUnique_shouldReturnErrorResponse() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new RecordConflictException("Email artem2549@gmail.com has already been taken by other user."));

        String requestBody = objectMapper.writeValueAsString(UserDto.builder()
                .email("alex2549@gmail.com")
                .username("b.alex")
                .firstName("Alex")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 14))
                .build());
        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Email artem2549@gmail.com has already been taken by other user.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    public void createUser_userUsernameIsNotUnique_shouldReturnErrorResponse() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new RecordConflictException("Username b.alex has already been taken by other user."));

        String requestBody = objectMapper.writeValueAsString(UserDto.builder()
                .email("alex2549@gmail.com")
                .username("b.alex")
                .firstName("Alex")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 14))
                .build());
        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Username b.alex has already been taken by other user.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
}