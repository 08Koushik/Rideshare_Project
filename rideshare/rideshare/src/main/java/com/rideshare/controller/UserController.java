package com.rideshare.controller;

import com.rideshare.entity.User;
import com.rideshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // Admin login
    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String email, @RequestParam String password) {
        if (userService.validateAdmin(email, password))
            return "Admin logged in successfully!";
        else
            return "Invalid admin credentials!";
    }

    // Onboard user
    @PostMapping("/admin/onboard")
    public User onboardUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String contact,
            @RequestParam String role,
            @RequestParam(required = false) String vehicle
    ) {
        return userService.onboardUser(name, email, contact, role, vehicle);
    }


    @PostMapping("/login")

    public Object userLogin(@RequestParam String email, @RequestParam String password) {
        try {

            User user = userService.userLogin(email, password);

            return user;
        } catch (RuntimeException e) {

            return e.getMessage();
        }
    }

    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        return userService.resetPassword(email, newPassword);
    }

    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete-all-users")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("All users deleted successfully!");
    }
}