package com.neev.ToDoMaticTW.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.requests.UserRequest;
import com.neev.ToDoMaticTW.security.CustomAuthenticationManager;
import com.neev.ToDoMaticTW.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    UserService userService;
    @MockBean
    CustomAuthenticationManager customAuthenticationManager;
    @Mock
    UserController userController;

    @BeforeEach
    void setUp() {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(context)
                        .apply(springSecurity())
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
                .andExpect(jsonPath("$.message").value("Username or password cannot be empty"));
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
                .andExpect(jsonPath("$.message").value("Username or password cannot be empty"));
    }

    @Test
    @DisplayName("Testing Registration should save user")
    void registrationOfUserShouldSaveUser() throws Exception {
        User user = new User("test_super", "password");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User Successfully Registered"));
    }

    @Test
    @DisplayName("Testing for registration of already taken username")
    void registrationOfDuplicateUsernamesShouldNotBeAllowed() throws Exception {
        User user = new User("test", "password");

        when(userService.doesUserExistsAlready(user.getUsername())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already taken"));
    }

    @Test
    @DisplayName("Testing Login with blank User")
    void LoginWithoutBodyShouldGiveBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Testing login with correct user details")
    void LoginWithUserDetailsShouldGiveOk() throws Exception {
        User user = new User("test", "password");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                mock(UsernamePasswordAuthenticationToken.class);

        when(customAuthenticationManager.authenticate(any())).thenReturn(usernamePasswordAuthenticationToken);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Testing login with correct user details")
    void LoginWithInvalidUsernameShouldGiveUnauthorized() throws Exception {
        User user = new User("test", "password");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                mock(UsernamePasswordAuthenticationToken.class);

        when(customAuthenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("User not found"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Testing login with correct user details")
    void LoginWithInvalidPasswordShouldGiveUnauthorized() throws Exception {
        User user = new User("test", "password");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                mock(UsernamePasswordAuthenticationToken.class);

        when(customAuthenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Wrong password"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Wrong password"));
    }

    @Test
    @DisplayName("Testing for access restriction for unauthenticated users")
    void UnAuthenticatedUsersShouldNotBeAccessAnyOtherAPIS() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/?username=test"))
                .andExpect(status().isUnauthorized());
    }
}