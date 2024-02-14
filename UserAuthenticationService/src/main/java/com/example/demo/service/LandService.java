package com.example.demo.service;

import com.example.demo.models.Land;
import com.example.demo.repository.LandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandService {

    private final LandRepository landRepository;

    public List<Land> getAllLands() {
        return landRepository.findAll();
    }

    public List<Land> getFarmerLands(String email){
        return landRepository.findByEmail(email);
    }

    public void addLand(Land land) {;
        landRepository.save(land);
    }


    @Transactional
    public void deleteLand(long landId) {
        Optional<Land> landOptional = landRepository.findById(landId);
        if(landOptional.isPresent()){
           landRepository.deleteById(landId);
        }else{
            throw new IllegalStateException("Land does not exists");
        }
    }

    public void addLocationToLand(String location, long landId) {
        Optional<Land> landOptional = landRepository.findById(landId);
        if(landOptional.isPresent()){
            Land land = landOptional.get();
            land.setLocation(location);
            landRepository.save(land);
        }

    }
}
