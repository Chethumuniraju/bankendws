package com.example.backend.service;

import com.example.backend.dto.PoliceDTO;
import com.example.backend.dto.PoliceLoginDTO;
import com.example.backend.model.Admin;
import com.example.backend.model.Complaint;
import com.example.backend.model.Police;
import com.example.backend.repository.AdminRepository;
import com.example.backend.repository.ComplaintRepository;
import com.example.backend.repository.PoliceRepository;
import com.example.backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

@Service
public class PoliceService {

    @Autowired
    private PoliceRepository policeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String GEOAPIFY_API_KEY = "49f1ab120d0b4477a74c9fb42fadbf49";
    private static final String GEOAPIFY_URL = "https://api.geoapify.com/v1/geocode/reverse?lat=%f&lon=%f&format=json&apiKey=%s";

    // Police Registration
    public ResponseEntity<String> registerPolice(String jwtToken, PoliceDTO policeDTO) {
        // Verify the token and extract the admin's email
        String adminEmail = jwtUtil.extractEmail(jwtToken);
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(adminEmail);
        if (optionalAdmin.isEmpty()) {
            throw new RuntimeException("Admin not found with email: " + adminEmail);
        }

        // Fetch address using Geoapify API
        String address = fetchAddressFromGeoapify(policeDTO.getLatitude(), policeDTO.getLongitude());
        if (address == null) {
            return ResponseEntity.status(500).body("Failed to fetch address from Geoapify");
        }

        // Create and save Police entity
        Police police = new Police();
        police.setName(policeDTO.getName());
        police.setLatitude(policeDTO.getLatitude());
        police.setLongitude(policeDTO.getLongitude());
        police.setAddress(address);
        police.setEmail(policeDTO.getEmail());
        police.setPassword(passwordEncoder.encode(policeDTO.getPassword())); // Encrypt password

        policeRepository.save(police);
        return ResponseEntity.ok("Police registered successfully");
    }

    // Police Login
    public ResponseEntity<?> loginPolice(PoliceLoginDTO loginDTO) {
        Optional<Police> optionalPolice = policeRepository.findByEmail(loginDTO.getEmail());

        if (optionalPolice.isPresent()) {
            Police police = optionalPolice.get();

            if (passwordEncoder.matches(loginDTO.getPassword(), police.getPassword())) {
                // Generate JWT token
                String token = jwtUtil.generateToken(police.getEmail());
                return ResponseEntity.ok().body("{ \"token\": \"" + token + "\" }");
            } else {
                return ResponseEntity.status(401).body("{ \"error\": \"Invalid credentials\" }");
            }
        } else {
            return ResponseEntity.status(404).body("{ \"error\": \"Police not found\" }");
        }
    }

    // Get Complaints Assigned to Police
    public List<Complaint> getPoliceComplaints(String jwtToken) {
        String email = jwtUtil.extractEmail(jwtToken);
        Optional<Police> policeOptional = policeRepository.findByEmail(email);

        if (policeOptional.isEmpty()) {
            throw new RuntimeException("Police not found with email: " + email);
        }

        Police police = policeOptional.get();
        return complaintRepository.findByPolice(police); // Fetch complaints assigned to this police officer
    }
    // Get all police stations
    public List<Police> getAllPoliceStations() {
    return policeRepository.findAll();
        }

        public ResponseEntity<String> updateComplaintStatus(String jwtToken, Long complaintId, String newStatus) {
            String email = jwtUtil.extractEmail(jwtToken);
            Optional<Police> optionalPolice = policeRepository.findByEmail(email);
    
            if (optionalPolice.isEmpty()) {
                return ResponseEntity.status(403).body("Unauthorized: Police officer not found.");
            }
    
            Optional<Complaint> optionalComplaint = complaintRepository.findById(complaintId);
            if (optionalComplaint.isEmpty()) {
                return ResponseEntity.status(404).body("Complaint not found.");
            }
    
            Complaint complaint = optionalComplaint.get();
            complaint.setStatus(newStatus); // Update Status
            complaintRepository.save(complaint);
    
            return ResponseEntity.ok("Complaint status updated successfully.");
        }
    // Fetch address from Geoapify API
    private String fetchAddressFromGeoapify(double latitude, double longitude) {
        try {
            String url = String.format(GEOAPIFY_URL, latitude, longitude, GEOAPIFY_API_KEY);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("results") && jsonResponse.getJSONArray("results").length() > 0) {
                return jsonResponse.getJSONArray("results").getJSONObject(0).getString("address_line2");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
