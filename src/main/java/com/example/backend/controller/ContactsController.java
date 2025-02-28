package com.example.backend.controller;

import com.example.backend.model.Contacts;
import com.example.backend.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/contacts")
public class ContactsController {

    @Autowired
    private ContactsService contactsService;

    @PostMapping("/add")
    public ResponseEntity<?> addContact(@RequestHeader("Authorization") String token, @RequestBody Contacts contact) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // Remove "Bearer " prefix
        }
        Contacts savedContact = contactsService.addContact(token, contact);
        return ResponseEntity.ok(savedContact);
    }
}
