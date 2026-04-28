package com.institute.skill7.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.institute.skill7.model.User;
import com.institute.skill7.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://fsad-cert-track-frontend.onrender.com"
})
public class AuthController {

    @Autowired
    private UserRepository repo;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        // normalize input
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPassword(user.getPassword().trim());

        return ResponseEntity.ok(repo.save(user));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        System.out.println("INPUT EMAIL: " + user.getEmail());
        System.out.println("INPUT PASSWORD: " + user.getPassword());

        String email = user.getEmail().trim().toLowerCase();
        String password = user.getPassword().trim();

        Optional<User> dbUser = repo.findByEmail(email);

        if (dbUser.isPresent()) {

            System.out.println("DB EMAIL: " + dbUser.get().getEmail());
            System.out.println("DB PASSWORD: " + dbUser.get().getPassword());

            String dbPassword = dbUser.get().getPassword().trim();

            if (dbPassword.equals(password)) {
                return ResponseEntity.ok(dbUser.get());
            }
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}