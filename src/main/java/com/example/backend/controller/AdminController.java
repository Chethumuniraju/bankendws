package com.example.backend.controller;

import java.util.List;
import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.PoliceDTO;
import com.example.backend.model.Admin;
import com.example.backend.model.Complaint;
import com.example.backend.model.Police;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.AdminService;
import com.example.backend.service.ComplaintService;
import com.example.backend.service.PoliceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("api/admins")
public class AdminController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private PoliceService policeService;

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
        boolean isAuthenticated = adminService.authenticateAdmin(authRequest.getEmail(), authRequest.getPassword());
        
        if (isAuthenticated) {
            String token = jwtUtil.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    // Register Police (With Token Authentication)
    @PostMapping("/police/register")
    public ResponseEntity<String> registerPolice(@RequestHeader("Authorization") String token, 
                                                 @RequestBody PoliceDTO policeDTO) {
        String jwtToken = token.replace("Bearer ", ""); // Remove "Bearer " prefix
        return policeService.registerPolice(jwtToken, policeDTO);
    }

    // Get All Complaints
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }
    @GetMapping("/police")
    public ResponseEntity<List<Police>> getAllPoliceStations() {
        return ResponseEntity.ok(policeService.getAllPoliceStations());
    }
    @GetMapping("/autocomplete")
    public ResponseEntity<String> getAutocomplete(@RequestParam("query") String query) {
        String response = adminService.getAutocompleteSuggestions(query);
        return ResponseEntity.ok(response);
    }

}
