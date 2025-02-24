package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintDTO {
    private String message;
    private double latitude;
    private double longitude;
}
