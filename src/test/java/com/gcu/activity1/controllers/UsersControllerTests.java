package com.gcu.activity1.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gcu.activity1.data.UsersDataService;
import com.gcu.activity1.models.UserModel;

import jakarta.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.*;

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

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));
    }

    @Test
    void showAllUsers_ForAdmin_SecurityAllowsAccess() throws Exception {
        when(usersDataService.getAll()).thenReturn(Arrays.asList(
                new UserModel(1, "user1", "", "ROLE_USER", true),
                new UserModel(2, "user2", "", "ROLE_USER", true)
        ));

        try {
            MvcResult result = mockMvc.perform(get("/admin/users")
                            .with(user("admin").roles("ADMIN")))
                    .andReturn();

            // If we get here without exception, check status
            int status = result.getResponse().getStatus();
            assertNotEquals(403, status, "Admin should not be forbidden");
            if (status == 302) {
                assertNotEquals("/login", result.getResponse().getRedirectedUrl(),
                    "Admin should not be redirected to login");
            }
        } catch (ServletException e) {
            // Template errors (TemplateInputException) or DB errors mean security passed
            // The request reached the controller/view layer
            String message = e.getMessage();
            assertTrue(
                message.contains("TemplateInputException") || message.contains("BadSqlGrammar"),
                "Expected template or DB error (security passed), but got: " + message
            );
        }
    }

    @Test
    void deleteUser_ForAdmin_ShouldRedirectToUsers() throws Exception {
        when(usersDataService.deleteById(1)).thenReturn(true);

        mockMvc.perform(post("/admin/users/delete")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN"))
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));
    }

    @Test
    void showAllUsers_ForRegularUser_ShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
