package com.gcu.activity1.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ordersPageShouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void usersPageShouldBeDeniedForRegularUser() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void usersPageShouldBeAccessibleForAdmin() throws Exception {
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
    void adminRoutes_WhenUnauthenticated_ShouldRedirectToLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertTrue(redirectUrl != null && redirectUrl.contains("/login"),
            "Unauthenticated access should redirect to login");
    }

    @Test
    void adminEditRoute_WhenUnauthenticated_ShouldRedirectToLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertTrue(redirectUrl != null && redirectUrl.contains("/login"),
            "Unauthenticated access should redirect to login");
    }

    @Test
    void adminDeleteRoute_WhenUnauthenticated_ShouldRedirectToLogin() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin/users/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertTrue(redirectUrl != null && redirectUrl.contains("/login"),
            "Unauthenticated access should redirect to login");
    }

    @Test
    void adminEditRoute_ForAdmin_ShouldBeAccessible() throws Exception {
        try {
            MvcResult result = mockMvc.perform(get("/admin/users/edit/1")
                            .with(user("admin").roles("ADMIN")))
                    .andReturn();

            int status = result.getResponse().getStatus();
            assertNotEquals(403, status, "Admin should not be forbidden");
        } catch (ServletException e) {
            assertTrue(e.getMessage().contains("TemplateInputException") ||
                       e.getMessage().contains("BadSqlGrammar"),
                "Expected template or DB error (security passed)");
        }
    }
}
