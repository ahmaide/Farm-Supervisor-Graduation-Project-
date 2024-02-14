package com.example.demo.repository;

import com.example.demo.models.Crop;
import com.example.demo.models.Land;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CropRespository extends JpaRepository<Crop, Long> {

    boolean existsByCropName(String cropName);
    Optional<Crop> findByCropName(String cropName);

    List<Crop> findByLandId(long landId);

}
