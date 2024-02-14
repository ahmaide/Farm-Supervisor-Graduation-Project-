package com.example.demo.service;

import com.example.demo.models.Pest;
import com.example.demo.repository.PestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PestService {
    private final PestRepository pestRepository;

    public List<Pest> getCropPests(long cropId) {
        return pestRepository.findPestByCropId(cropId);
    }

    public void addPest(Pest pest) {
        pestRepository.save(pest);
    }


    @Transactional
    public void deletePest(long pestId) {
        Optional<Pest> pestOptional = pestRepository.findById(pestId);
        if(pestOptional.isPresent()){
            pestRepository.deleteById(pestId);
        }else{
            throw new IllegalStateException("Pest does not exists");
        }
    }
}
