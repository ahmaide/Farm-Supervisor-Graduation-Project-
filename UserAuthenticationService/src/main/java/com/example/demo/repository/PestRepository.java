package com.example.demo.repository;

import com.example.demo.models.Pest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PestRepository extends JpaRepository<Pest, Long> {
    List<Pest> findPestByCropId(long cropId);
}
