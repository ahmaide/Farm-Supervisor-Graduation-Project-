package com.example.demo.controller;

import com.example.demo.models.Land;
import com.example.demo.service.LandService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/land")
@CrossOrigin
@RequiredArgsConstructor
public class LandController {

    private final LandService landService;

    @GetMapping("all/{email}")
    public List<Land> getAllLands(@PathVariable("email") String email){
        return landService.getFarmerLands(email);
    }

    @PostMapping("add")
    public void addLand(@RequestBody Land land){
        landService.addLand(land);
    }

    @DeleteMapping("delete/{landId}")
    public void deleteLand(@PathVariable("landId") long landId){
        landService.deleteLand(landId);
    }

}
