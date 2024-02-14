package com.example.demo.controller;

import com.example.demo.models.Farmer;
import com.example.demo.models.Specialization;
import com.example.demo.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/specialization")
@CrossOrigin
public class SpecializationController {

    private final SpecializationService specializationService;

    @PostMapping("/add")
    public Specialization addSpecialization(@RequestBody Specialization specialization) {
        return specializationService.save(specialization);
    }

    @DeleteMapping("/delete/{specId}")
    public void deleteCrop(@PathVariable("specId") long specId){
        specializationService.delete(specId);
    }

    @GetMapping("/type/{type}")
    public List<Specialization> getSpecializationsByType(@PathVariable String type) {
        return specializationService.getByType(type);
    }

    @GetMapping("/farmer/{email}")
    public List<Specialization> getSpecializationsByEmail(@PathVariable String email) {
        return specializationService.getByEmail(email);
    }

    @GetMapping("/typeFarmers/{type}")
    public List<Farmer> getSpecializationFarmers(@PathVariable String type){
        return specializationService.getTypeFarmers(type);
    }
}
