package com.example.backend.service;

import com.example.backend.model.Admin;
import com.example.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin registerAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword())); // Encrypt password
        return adminRepository.save(admin);
    }

    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public boolean authenticateAdmin(String email, String rawPassword) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        return admin.isPresent() && passwordEncoder.matches(rawPassword, admin.get().getPassword());
    }
   


    private static final String GEOAPIFY_API_KEY = "49f1ab120d0b4477a74c9fb42fadbf49";
    private static final String GEOAPIFY_AUTOCOMPLETE_URL = "https://api.geoapify.com/v1/geocode/autocomplete?text=%s&format=json&apiKey=" + GEOAPIFY_API_KEY;

    public String getAutocompleteSuggestions(String query) {
        String url = String.format(GEOAPIFY_AUTOCOMPLETE_URL, query);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

}

