package com.example.demo.repository;

import com.example.demo.models.Land;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LandRepository extends JpaRepository<Land, Long> {
    List<Land> findByEmail(String email);
}
