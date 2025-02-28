package com.example.backend.repository;

import com.example.backend.model.Police;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PoliceRepository extends JpaRepository<Police, Long> {
    Optional<Police> findByEmail(String email);
}
