package com.example.backend.service;

import com.example.backend.model.Contacts;
import com.example.backend.model.User;
import com.example.backend.repository.ContactsRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactsService {

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Add a new contact
    public Contacts addContact(String token, Contacts contact) {
        String email = jwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            contact.setUser(user);
            return contactsRepository.save(contact);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Get all contacts for a user
    public List<Contacts> getUserContacts(String token) {
        String email = jwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println(contactsRepository.findByUser(user));
            return contactsRepository.findByUser(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Update an existing contact
    public Contacts updateContact(String token, Long contactId, Contacts updatedContact) {
        String email = jwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Contacts> contactOptional = contactsRepository.findById(contactId);

            if (contactOptional.isPresent()) {
                Contacts contact = contactOptional.get();

                // Ensure the contact belongs to the authenticated user
                if (!contact.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("Unauthorized to update this contact");
                }

                contact.setContactName(updatedContact.getContactName());
                contact.setContactNo(updatedContact.getContactNo());

                return contactsRepository.save(contact);
            } else {
                throw new RuntimeException("Contact not found");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Delete a contact
    public void deleteContact(String token, Long contactId) {
        String email = jwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Contacts> contactOptional = contactsRepository.findById(contactId);

            if (contactOptional.isPresent()) {
                Contacts contact = contactOptional.get();

                // Ensure the contact belongs to the authenticated user
                if (!contact.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("Unauthorized to delete this contact");
                }

                contactsRepository.delete(contact);
            } else {
                throw new RuntimeException("Contact not found");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
