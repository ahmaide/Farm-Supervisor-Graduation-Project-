package com.example.demo.repository;

import com.example.demo.models.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    List<Specialization> findByEmail(String email);

    List<Specialization> findByType(String type);
}
