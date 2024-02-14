package com.example.demo.controller;

import com.example.demo.models.Crop;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.CropService;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/crop")
@CrossOrigin
public class CropController {

    private final CropService cropService;

    @PostMapping("add")
    public void addCrop(@RequestBody Crop crop){
                        cropService.addCrop(crop);
    }

    @GetMapping("all/{landId}")
    public List<Crop> getAllCrops(@PathVariable("landId") long landId){
        return cropService.getLandsCrops(landId);
    }


    @DeleteMapping("delete/{cropId}")
    public void deleteCrop(@PathVariable("cropId") long cropId){
        cropService.deleteCrop(cropId);
    }

}
