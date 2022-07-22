package com.neev.ToDoMaticTW.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.requests.UserRequest;
import com.neev.ToDoMaticTW.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    UserService userService;

    @BeforeEach
    void setUp() {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("Testing Registration without any user")
    void registrationWithBlankUserShouldGiveStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/register"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Testing Registration without username")
    void registrationWithUsernameAsNullShouldGiveBadRequestStatusAlongWithMessage() throws Exception {
        User user = new User(null, "password");
        UserRequest userRequest = new UserRequest(user.getUsername(), user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or Password cannot be empty"));
    }

    @Test
    @DisplayName("Testing Registration without password")
    void registrationWithPasswordAsNullShouldGiveBadRequestStatusAlongWithMessage() throws Exception {
        User user = new User("null", null);
        UserRequest userRequest = new UserRequest(user.getUsername(), user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or Password cannot be empty"));
    }

    @Test
    @DisplayName("Testing Registration should save user")
    void registrationOfUserShouldSaveUser() throws Exception {
        User user = new User("test_super", "password");
        UserRequest userRequest = new UserRequest(user.getUsername(), user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Successfully Registered"));
    }

    @Test
    @DisplayName("Testing for registration of already taken username")
    void registrationOfDuplicateUsernamesShouldNotBeAllowed() throws Exception {
        User user = new User("test", "password");
        UserRequest userRequest = new UserRequest(user.getUsername(), user.getPassword());

        when(userService.doesUserExistsAlready(user.getUsername())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already taken"));
    }
}