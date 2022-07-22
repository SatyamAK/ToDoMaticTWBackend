package com.neev.ToDoMaticTW.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neev.ToDoMaticTW.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

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
    void registrationWithUsernameAsNullShouldGiveBadRequestStatusAlongWithMessage() throws Exception {
        User user = new User(null, "password");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or Password cannot be empty"));
    }

    @Test
    void registrationWithPasswordAsNullShouldGiveBadRequestStatusAlongWithMessage() throws Exception {
        User user = new User("null", null);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or Password cannot be empty"));
    }
}