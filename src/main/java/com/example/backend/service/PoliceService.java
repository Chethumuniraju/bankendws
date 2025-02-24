package com.example.backend.service;

import com.example.backend.dto.PoliceDTO;
import com.example.backend.model.Police;
import com.example.backend.repository.PoliceRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
@RequiredArgsConstructor
public class PoliceService {

    private final PoliceRepository policeRepository;
    private final RestTemplate restTemplate;

    public Police registerPolice(PoliceRequest request) {
        String geoApiUrl = "https://api.geoapify.com/v1/geocode/reverse?lat="
                + request.getLatitude() + "&lon=" + request.getLongitude()
                + "&format=json&apiKey=49f1ab120d0b4477a74c9fb42fadbf49";

        // Fetch Address from GeoAPI
        ResponseEntity<String> response = restTemplate.getForEntity(geoApiUrl, String.class);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        String address = jsonResponse.getJSONArray("results").getJSONObject(0).getString("address_line2");

        // Create Police Entity
        Police police = new Police();
        police.setName(request.getName());
        police.setLatitude(request.getLatitude());
        police.setLongitude(request.getLongitude());
        police.setAddress(address);
        police.setPassword(request.getPassword());
        police.setEmail(request.getEmail());

        return policeRepository.save(police);
    }
}
