package com.gcu.activity1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gcu.activity1.data.UsersDataService;
import com.gcu.activity1.models.UserModel;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    @Autowired
    private UsersDataService usersDataService;

    public UserAdminController(UsersDataService usersDataService) {
        this.usersDataService = usersDataService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", usersDataService.getAll());
        model.addAttribute("title", "User Management");
        return "admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable int id, Model model) {
        model.addAttribute("user", usersDataService.getById(id));
        model.addAttribute("title", "Edit User");
        return "admin/editUser";
    }

    @PostMapping("/edit")
    public String processEdit(UserModel user) {
        usersDataService.update(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        model.addAttribute("user", usersDataService.getById(id));
        model.addAttribute("title", "Delete User");
        return "admin/deleteUser";
    }

    @PostMapping("/delete")
    public String processDelete(@RequestParam int id) {
        usersDataService.deleteById(id);
        return "redirect:/admin/users";
    }
}
