package com.example.demo.service;

import com.example.demo.models.Farmer;
import com.example.demo.models.Specialization;
import com.example.demo.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    private final FarmerService farmerService;

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }


    public List<Specialization> getByType(String type) {
        return specializationRepository.findByType(type);
    }

    public List<Specialization> getByEmail(String email) {
        return specializationRepository.findByEmail(email);
    }

    public void delete(Long id) {
        boolean exists = specializationRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Specialization with id " + id + " does not exist.");
        }
        specializationRepository.deleteById(id);
    }

    public List<Farmer> getTypeFarmers(String type){
        List<Specialization> specializations = specializationRepository.findByType(type);
        List<Farmer> farmers = new ArrayList<>();
        for (Specialization specialization : specializations) {
            Farmer farmer = farmerService.findByEmail(specialization.getEmail());
            farmers.add(farmer);
        }
        return  farmers;
    }
}
