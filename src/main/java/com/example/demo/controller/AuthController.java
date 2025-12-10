package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // isPresent(): Optional이 비어있지 않으면 true
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User newUser = new User(username, password);
        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String username = body.get("username");
        String password = body.get("password");

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            String token = jwtUtil.generateToken(username);

            org.springframework.http.ResponseCookie cookie = org.springframework.http.ResponseCookie
                    .from("accessToken", token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Lax") // Use Lax for better compatibility on HTTP
                    .build();

            response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());

            Map<String, String> resBody = new HashMap<>();
            resBody.put("message", "Login successful");
            resBody.put("username", username);
            return ResponseEntity.ok(resBody);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(value = "accessToken", required = false) String token) {
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            Map<String, String> response = new HashMap<>();
            response.put("username", jwtUtil.extractUsername(token));
            response.put("message", "Authenticated");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}
