package com.techup.travel_app.controller;

import com.techup.travel_app.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map; // เพิ่ม import

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(@AuthenticationPrincipal User user) {
        // แก้ตรงนี้ให้ return เป็น Map หรือ Object ที่ Spring จะแปลงเป็น JSON
        return ResponseEntity.ok().body(Map.of(
            "message", "User synced successfully",
            "email", (user != null ? user.getEmail() : "Unknown")
        ));
    }
}

