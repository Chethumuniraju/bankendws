package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Reference to the user who filed the complaint

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "police_id", nullable = false)
    private Police police; // Reference to the nearest police station

    @Column(nullable = false)
    private String status; // Example: "Pending", "In Progress", "Resolved"
}
