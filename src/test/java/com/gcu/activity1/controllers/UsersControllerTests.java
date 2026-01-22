package com.gcu.activity1.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.gcu.activity1.data.UsersDataService;
import com.gcu.activity1.models.UserModel;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersDataService usersDataService;

    @Test
    void processRegistration_WithValidData_ShouldRedirectToLogin() throws Exception {
        when(usersDataService.usernameExists("newuser")).thenReturn(false);
        when(usersDataService.create(any(UserModel.class))).thenReturn(new UserModel());

        mockMvc.perform(post("/processRegistration")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void showAllUsers_ForAdmin_ShouldReturnAllUsersView() throws Exception {
        when(usersDataService.getAll()).thenReturn(Arrays.asList(
                new UserModel(1, "user1", "", "ROLE_USER", true),
                new UserModel(2, "user2", "", "ROLE_USER", true)
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_ForAdmin_ShouldRedirectToUsers() throws Exception {
        when(usersDataService.deleteById(1)).thenReturn(true);

        mockMvc.perform(get("/users/deleteUser/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void showAllUsers_ForRegularUser_ShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }
}
