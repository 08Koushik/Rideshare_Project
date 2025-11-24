package com.rideshare.controller;

import com.rideshare.dto.OnboardUserRequest;
import com.rideshare.dto.AdminReportDTO;
import com.rideshare.entity.User;
import com.rideshare.service.AdminReportingService;
import com.rideshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminReportingService adminReportingService;

    // ---------- ADMIN LOGIN ----------
    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestParam String email, @RequestParam String password) {
        boolean ok = userService.validateAdmin(email, password);
        return ok
                ? ResponseEntity.ok("Admin logged in successfully!")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin credentials!");
    }

    // ---------- ADMIN ONBOARD (Driver/Passenger/Admin) ----------
    @PostMapping("/admin/onboard")
    public ResponseEntity<?> onboardUser(@RequestBody OnboardUserRequest request) {
        try {
            User saved = userService.onboardUser(
                    request.getName(),
                    request.getEmail(),
                    request.getContact(),
                    request.getRole(),
                    request.getVehicle(),
                    request.getDriverLicenseNumber(),
                    request.getAadharNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalStateException ex) {
            // From service -> EMAIL_EXISTS or validation messages
            if ("EMAIL_EXISTS".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            // DB unique constraint fallback
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to onboard user");
        }
    }

    // ---------- PUBLIC REGISTER (forces non-admin) ----------
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody OnboardUserRequest request) {
        try {
            String role = request.getRole();
            if (role == null || role.equalsIgnoreCase("ADMIN")) {
                role = "PASSENGER";
            }
            User saved = userService.onboardUser(
                    request.getName(),
                    request.getEmail(),
                    request.getContact(),
                    role,
                    request.getVehicle(),
                    request.getDriverLicenseNumber(),
                    request.getAadharNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalStateException ex) {
            if ("EMAIL_EXISTS".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }

    // ---------- USER LOGIN ----------
    // Frontend expects either:
    //  - "FIRST_LOGIN" (text) OR
    //  - User JSON OR
    //  - error text
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestParam String email, @RequestParam String password) {
        try {
            Object result = userService.userLogin(email, password);
            return ResponseEntity.ok(result); // could be User object
        } catch (RuntimeException e) {
            // propagate FIRST_LOGIN or error text
            if ("FIRST_LOGIN".equals(e.getMessage())) {
                return ResponseEntity.ok("FIRST_LOGIN");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ---------- RESET PASSWORD ----------
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        String msg = userService.resetPassword(email, newPassword);
        if ("User not found!".equalsIgnoreCase(msg)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        return ResponseEntity.ok(msg);
    }

    // ---------- ADMIN: GET ALL USERS ----------
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ---------- DELETE ALL (except admin) ----------
    @DeleteMapping("/delete-all-users")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("All users deleted successfully!");
    }

    // ---------- ADMIN REPORT ----------
    @GetMapping("/admin/report")
    public ResponseEntity<AdminReportDTO> getReport() {
        return ResponseEntity.ok(adminReportingService.getSystemReport());
    }
}
