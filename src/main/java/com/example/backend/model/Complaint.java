package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "complaint")
@Getter
@Setter
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String complaint;
    private double latitude;
    private double longitude;
    private String address;
    private String message;

    @ManyToOne
    @JoinColumn(name = "police_id", nullable = false)
    private Police police;  // Changed police_id to Police entity

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // Default status

    public enum Status {
        PENDING, ONGOING, DROPPED, FINISHED
    }
}
