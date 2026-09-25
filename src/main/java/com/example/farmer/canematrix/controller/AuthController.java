package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.User;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.repository.UserRepository;
import com.example.farmer.canematrix.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. फॅक्टरी युजर (Admin / Clerk) नोंदणी करणे
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole()); // ROLE_ADMIN किंवा ROLE_CLERK

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully as " + userRequest.getRole());
    }

    // 2. फॅक्टरी युजर लॉगिन (Admin / Clerk Login)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "role", user.getRole(),
                        "message", "Login Successful!"
                ));
            }
        }

        return ResponseEntity.status(401).body("Invalid Username or Password!");
    }

    // 3. शेतकरी लॉगिन (Farmer Login)
    @PostMapping("/farmer-login")
    public ResponseEntity<?> loginFarmer(@RequestBody Map<String, String> loginRequest) {
        String farmerCode = loginRequest.get("farmerCode");
        String password = loginRequest.get("password");

        Optional<Farmer> optionalFarmer = farmerRepository.findByFarmerCode(farmerCode);

        if (optionalFarmer.isPresent()) {
            Farmer farmer = optionalFarmer.get();

            if (passwordEncoder.matches(password, farmer.getPassword())) {
                String token = jwtUtil.generateToken(farmer.getFarmerCode(), farmer.getRole());

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "role", farmer.getRole(),
                        "message", "Farmer Login Successful!"
                ));
            }
        }

        return ResponseEntity.status(401).body("Invalid Farmer Code or Password!");
    }
}