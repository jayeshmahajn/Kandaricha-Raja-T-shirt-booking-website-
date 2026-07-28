package com.mandal.tshirt.controller;

import com.mandal.tshirt.config.AdminAuth;
import com.mandal.tshirt.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminAuth adminAuth;

    public AdminController(AdminAuth adminAuth) {
        this.adminAuth = adminAuth;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        if (adminAuth.isValid(key)) {
            return ResponseEntity.ok(new ApiResponse(true, "Welcome, Karyakarta!"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Wrong password"));
    }
}
