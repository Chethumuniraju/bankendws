package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoliceDTO {
    private String name;
    private double latitude;
    private double longitude;
    private String password;
    private String email;
}
