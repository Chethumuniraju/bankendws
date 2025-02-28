package com.example.backend.controller;
import java.util.List;
import com.example.backend.dto.PoliceLoginDTO;
import com.example.backend.model.Complaint;
import com.example.backend.service.PoliceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/police")
public class PoliceController {

    private final PoliceService policeService;

    public PoliceController(PoliceService policeService) {
        this.policeService = policeService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPolice(@RequestBody PoliceLoginDTO loginDTO) {
        return policeService.loginPolice(loginDTO);
    }
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAssignedComplaints(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        List<Complaint> complaints = policeService.getPoliceComplaints(jwtToken);
        return ResponseEntity.ok(complaints);
    }
    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<String> updateComplaintStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam("status") String status) {

        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        return policeService.updateComplaintStatus(jwtToken, id, status);
    }

}
    