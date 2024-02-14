package com.example.demo.service;

import com.example.demo.models.Crop;
import com.example.demo.models.Land;
import com.example.demo.repository.CropRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRespository cropRespository;


    public void addCrop(Crop crop) {
        cropRespository.save(crop);
    }

    public List<Crop> getLandsCrops(long landId) {
        return cropRespository.findByLandId(landId);
    }

    @Transactional
    public void deleteCrop(long cropId) {
        Optional<Crop> cropOptional = cropRespository.findById(cropId);
        if(cropOptional.isPresent()){
                cropRespository.deleteById(cropId);
        }
        else{
            throw new IllegalStateException("Crop does not exists!");
        }
    }

}
