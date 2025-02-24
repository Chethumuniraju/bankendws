package com.example.backend.service;

import com.example.backend.dto.PoliceDTO;
import com.example.backend.model.Admin;
import com.example.backend.model.Police;
import com.example.backend.repository.AdminRepository;
import com.example.backend.repository.PoliceRepository;
import com.example.backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import org.json.JSONObject;

@Service
public class PoliceService {

    @Autowired
    private PoliceRepository policeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String GEOAPIFY_API_KEY = "49f1ab120d0b4477a74c9fb42fadbf49";
    private static final String GEOAPIFY_URL = "https://api.geoapify.com/v1/geocode/reverse?lat=%f&lon=%f&format=json&apiKey=%s";

    public ResponseEntity<String> registerPolice(String jwtToken, PoliceDTO policeDTO) {
        // Verify the token and extract the admin's email
        String adminEmail = jwtUtil.extractEmail(jwtToken);
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(adminEmail);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            // Now you can use the admin object
        } else {
            throw new RuntimeException("Admin not found with email: " + adminEmail);
        }

        // Fetch address using Geoapify API
        String address = fetchAddressFromGeoapify(policeDTO.getLatitude(), policeDTO.getLongitude());
        if (address == null) {
            return ResponseEntity.status(500).body("Failed to fetch address from Geoapify");
        }

        // Create Police entity and save it
        Police police = new Police();
        police.setName(policeDTO.getName());
        police.setLatitude(policeDTO.getLatitude());
        police.setLongitude(policeDTO.getLongitude());
        police.setAddress(address);
        police.setPassword(policeDTO.getPassword());
        police.setEmail(policeDTO.getEmail());

        policeRepository.save(police);
        return ResponseEntity.ok("Police registered successfully");
    }

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
