package com.splitter.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splitter.userservice.model.ErrorResponse;
import com.splitter.userservice.model.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/data/user-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/data/delete-user-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldReturnListOfUsers() throws Exception {
        String response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto[] result = mapper.readValue(response, UserDto[].class);
        assertThat(result.length).isEqualTo(2);
    }

    @Test
    public void shouldReturnOneUserById() throws Exception {
        String response = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto result = mapper.readValue(response, UserDto.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void shouldCreateNewUserAndReturnIt() throws Exception {
        String requestBody = mapper.writeValueAsString(UserDto.builder()
                .email("alex2549@gmail.com")
                .username("b.alex")
                .firstName("Alex")
                .lastName("Baranovskyi")
                .dateOfBirth(LocalDate.of(1994, 8, 14))
                .build());
        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = mapper.readValue(response, UserDto.class);
        assertThat(createdUser.getId()).isNotNull();
    }

    @Test
    public void shouldReturnErrorResponseIfUserDoesNotExist() throws Exception {
        String response = mockMvc.perform(get("/users/100500"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
        assertThat(result.getMessage()).isEqualTo("User 100500 does not exist.");
        assertThat(result.getTimestamp()).isNotNull();
    }

    @Test
    public void shouldReturnErrorResponseOfCreateUserWithFutureDateOfBirth() throws Exception {
        String requestBody = mapper.writeValueAsString(UserDto.builder()
                .email("alex_unique@gmail.com")
                .username("b.alex_unique")
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

        ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Date of birth 9999-08-14 is invalid.");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    public void shouldReturnErrorResponseIfCreateUserWithDuplicateEmail() throws Exception {
        String requestBody = mapper.writeValueAsString(UserDto.builder()
                .email("artem2549@gmail.com")
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

        ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Email artem2549@gmail.com has already been taken by other user.");
    }

    @Test
    public void shouldReturnErrorResponseIfCreateUserWithDuplicateUsername() throws Exception {
        String requestBody = mapper.writeValueAsString(UserDto.builder()
                .email("alex@gmail.com")
                .username("b.artem")
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

        ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo("Username b.artem has already been taken by other user.");
    }
}
