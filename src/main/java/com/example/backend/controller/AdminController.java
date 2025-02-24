package com.example.backend.controller;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.PoliceDTO;
import com.example.backend.model.Admin;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.AdminService;
import com.example.backend.service.PoliceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register Admin
    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        Admin savedAdmin = adminService.registerAdmin(admin);
        return ResponseEntity.ok(savedAdmin);
    }

    // Login Admin (Generate JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        if (adminService.authenticateAdmin(authRequest.getEmail(), authRequest.getPassword())) {
            String token = jwtUtil.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @Autowired
    private PoliceService policeService;

    // Register Police (With Token Authentication)
    @PostMapping("/police/register")
    public ResponseEntity<String> registerPolice(@RequestHeader("Authorization") String token, 
                                                 @RequestBody PoliceDTO policeDTO) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        return policeService.registerPolice(jwtToken, policeDTO);
    }
}
