package com.example.backend.repository;

import com.example.backend.model.Complaint;
import com.example.backend.model.Police;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPolice(Police police); // Fetch complaints assigned to a specific Police object
}
