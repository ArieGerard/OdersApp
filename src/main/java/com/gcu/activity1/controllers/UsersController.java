package com.gcu.activity1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.activity1.data.UsersDataService;
import com.gcu.activity1.models.Mapper;
import com.gcu.activity1.models.RegistrationModel;
import com.gcu.activity1.models.UserModel;

import jakarta.validation.Valid;

@Controller
public class UsersController {

    @Autowired
    private UsersDataService usersDataService;

    public UsersController(UsersDataService usersDataService) {
        this.usersDataService = usersDataService;
    }

    @GetMapping("/register")
    public String showRegistration(Model model) {
        model.addAttribute("registration", new RegistrationModel());
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping("/processRegistration")
    public String processRegistration(
            @Valid RegistrationModel registration,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Register");
            return "register";
        }

        if (!registration.passwordsMatch()) {
            bindingResult.rejectValue("confirmPassword", "error.registration", "Passwords do not match");
            model.addAttribute("title", "Register");
            return "register";
        }

        if (usersDataService.usernameExists(registration.getUsername())) {
            bindingResult.rejectValue("username", "error.registration", "Username already exists");
            model.addAttribute("title", "Register");
            return "register";
        }

        UserModel user = Mapper.registrationToUser(registration);
        usersDataService.create(user);

        return "redirect:/login?registered=true";
    }

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("users", usersDataService.getAll());
        model.addAttribute("title", "All Users");
        return "allUsers";
    }

    @GetMapping("/users/showUser/{id}")
    public String showOneUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", usersDataService.getById(id));
        model.addAttribute("title", "User Details");
        return "oneUser";
    }

    @GetMapping("/users/editUser/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", usersDataService.getById(id));
        model.addAttribute("title", "Edit User");
        return "editUser";
    }

    @PostMapping("/users/processEditUser")
    public String processEditUser(UserModel user, BindingResult bindingResult, Model model) {
        usersDataService.update(user);
        return "redirect:/users";
    }

    @GetMapping("/users/newUser")
    public String newUser(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("title", "New User");
        return "newUser";
    }

    @PostMapping("/users/processNewUser")
    public String processNewUser(@Valid UserModel user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "New User");
            return "newUser";
        }
        usersDataService.create(user);
        return "redirect:/users";
    }

    @GetMapping("/users/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id, Model model) {
        usersDataService.deleteById(id);
        return "redirect:/users";
    }
}
