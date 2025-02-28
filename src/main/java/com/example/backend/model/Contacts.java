package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contacts")
@Getter
@Setter
public class Contacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "contact_no", nullable = false)
    private String contactNo;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    public String getContactNo() {
        return contactNo;
    }
}
