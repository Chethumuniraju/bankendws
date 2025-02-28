package com.example.backend.service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.example.backend.dto.ComplaintDTO;
import com.example.backend.model.Complaint;
import com.example.backend.model.Contacts;
import com.example.backend.model.Police;
import com.example.backend.model.User;
import com.example.backend.repository.ComplaintRepository;
import com.example.backend.repository.PoliceRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;
import com.example.backend.service.ContactsService;
import io.github.cdimascio.dotenv.Dotenv;


@Service
public class ComplaintService {
    @Autowired
    private ContactsService contactsService;
    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PoliceRepository policeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
    private static final Dotenv dotenv = Dotenv.load();

    private static final String GEOAPIFY_API_KEY = "49f1ab120d0b4477a74c9fb42fadbf49"; // Replace with actual key
    private static final String GEOAPIFY_URL = "https://api.geoapify.com/v1/geocode/reverse?lat=%f&lon=%f&format=json&apiKey=%s";

   

    public ResponseEntity<String> registerComplaint(String jwtToken, ComplaintDTO complaintDTO) {
        // Extract email from JWT token
        String userEmail = jwtUtil.extractEmail(jwtToken);
        System.out.println("Extracted Email: " + userEmail);
    
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        System.out.println("User Found: " + optionalUser.isPresent());
    
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = optionalUser.get();
        System.out.println("User ID: " + user.getId());
    
        // Fetch contacts for the user
        List<Contacts> contactsList = contactsService.getUserContacts(jwtToken);
        System.out.println("Here is our contacts list: " + contactsList);
    
        


        // Fetch address from Geoapify
        String address = fetchAddressFromGeoapify(complaintDTO.getLatitude(), complaintDTO.getLongitude());
        if (address == null) {
            return ResponseEntity.status(500).body("Failed to fetch address from Geoapify");
        }

        // Find the nearest police station
        Police nearestPolice = findNearestPoliceStation(complaintDTO.getLatitude(), complaintDTO.getLongitude());
        if (nearestPolice == null) {
            return ResponseEntity.status(404).body("No nearby police station found");
        }

        // Create and save the complaint
        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setMessage(complaintDTO.getMessage());
        complaint.setLatitude(complaintDTO.getLatitude());
        complaint.setLongitude(complaintDTO.getLongitude());
        complaint.setAddress(address);
        complaint.setPolice(nearestPolice);
        complaint.setStatus("Pending");

        complaintRepository.save(complaint);

        // **Send SMS notifications**
        String messageBody = "Emergency Alert! "+ address ;

        for (Contacts contact : contactsList) {
            String phoneNumber = "+91" + contact.getContactNo();
            System.out.println("Sending SMS to: " + phoneNumber);
            sendSmsNotification(phoneNumber, messageBody);
        }

        return ResponseEntity.ok("Complaint registered successfully, contacts notified.");
    }

    // private void sendSmsNotification(String to, String messageBody) {
    //     Message.creator(
    //             new PhoneNumber(to),
    //             new PhoneNumber(TWILIO_PHONE_NUMBER),
    //             messageBody
    //     ).create();
    // }


    // Fetch address from Geoapify
    private String fetchAddressFromGeoapify(double latitude, double longitude) {
        try {
            String url = String.format(GEOAPIFY_URL, latitude, longitude, GEOAPIFY_API_KEY);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("results") && jsonResponse.getJSONArray("results").length() > 0) {
                return jsonResponse.getJSONArray("results").getJSONObject(0).getString("address_line2");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Find the nearest police station using Haversine formula
    private Police findNearestPoliceStation(double userLat, double userLon) {
        List<Police> allPoliceStations = policeRepository.findAll();
        Police nearestPolice = null;
        double minDistance = Double.MAX_VALUE;

        for (Police police : allPoliceStations) {
            double distance = calculateDistance(userLat, userLon, police.getLatitude(), police.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestPolice = police;
            }
        }
        return nearestPolice;
    }

    // Haversine formula to calculate distance
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }
}
