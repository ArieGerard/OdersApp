package com.gcu.activity1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gcu.activity1.data.UsersDataService;
import com.gcu.activity1.models.Mapper;
import com.gcu.activity1.models.RegistrationModel;
import com.gcu.activity1.models.UserModel;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UsersDataService usersDataService;

    public AuthController(UsersDataService usersDataService) {
        this.usersDataService = usersDataService;
    }

    // === Login ===

    @GetMapping("/login")
    public String showLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully");
        }

        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful. Please login.");
        }

        model.addAttribute("title", "Login");
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // === Registration ===

    @GetMapping("/register")
    public String showRegistration(Model model) {
        model.addAttribute("registration", new RegistrationModel());
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid RegistrationModel registration,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
model.addAttribute("registration", registration);
            model.addAttribute("title", "Register");
            return "register";
        }

        if (!registration.passwordsMatch()) {
            bindingResult.rejectValue("confirmPassword", "error.registration", "Passwords do not match");
            model.addAttribute("registration", registration);
            model.addAttribute("title", "Register");
            return "register";
        }

        if (usersDataService.usernameExists(registration.getUsername())) {
            bindingResult.rejectValue("username", "error.registration", "Username already exists");
            model.addAttribute("registration", registration);
            model.addAttribute("title", "Register");
            return "register";
        }

        UserModel user = Mapper.registrationToUser(registration);
        usersDataService.create(user);

        return "redirect:/login?registered=true";
    }
}
