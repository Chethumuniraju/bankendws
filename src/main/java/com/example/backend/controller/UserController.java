package com.example.backend.controller;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.ComplaintDTO;
import com.example.backend.model.Complaint;
import com.example.backend.model.Contacts;
import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.ComplaintService;
import com.example.backend.service.ContactsService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // Login User (Generate JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        if (userService.authenticateUser(authRequest.getEmail(), authRequest.getPassword())) {
            String token = jwtUtil.generateToken(authRequest.getEmail());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    // Add a Contact
    @PostMapping("/contacts")
    public ResponseEntity<Contacts> addContact(@RequestHeader("Authorization") String token, 
                                               @RequestBody Contacts contact) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        Contacts savedContact = contactsService.addContact(jwtToken, contact);
        return ResponseEntity.ok(savedContact);
    }

    // Get All Contacts
    @GetMapping("/contacts")
    public ResponseEntity<List<Contacts>> getUserContacts(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        List<Contacts> contacts = contactsService.getUserContacts(jwtToken);
        return ResponseEntity.ok(contacts);
    }

    // Update Contact
    @PutMapping("/contacts/{contactId}")
    public ResponseEntity<Contacts> updateContact(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long contactId,
                                                  @RequestBody Contacts updatedContact) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        Contacts contact = contactsService.updateContact(jwtToken, contactId, updatedContact);
        return ResponseEntity.ok(contact);
    }

    // Delete Contact
    @DeleteMapping("/contacts/{contactId}")
    public ResponseEntity<String> deleteContact(@RequestHeader("Authorization") String token,
                                                @PathVariable Long contactId) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        contactsService.deleteContact(jwtToken, contactId);
        return ResponseEntity.ok("Contact deleted successfully");
    }

    // Register a Complaint
@PostMapping("/register-complaint")
public ResponseEntity<String> registerComplaint(    
    
        @RequestHeader("Authorization") String token,
        @RequestBody ComplaintDTO complaintDTO) {
            System.out.println("Extracted Email:");
    return complaintService.registerComplaint(token.substring(7), complaintDTO);
}

}
