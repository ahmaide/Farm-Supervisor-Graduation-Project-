package com.example.demo.controller;

import com.example.demo.models.Pest;
import com.example.demo.service.PestService;
import com.example.demo.service.PestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/pest")
@CrossOrigin
@RequiredArgsConstructor
public class PestController {

    private final PestService pestService;

    @GetMapping("all/{cropId}")
    public List<Pest> getAllPests(@PathVariable long cropId){
        return pestService.getCropPests(cropId);
    }

    @PostMapping("add")
    public void addPest(@RequestBody Pest pest){
        pestService.addPest(pest);
    }


    @DeleteMapping("delete/{pestId}")
    public void deletePest(@PathVariable("pestId") long pestId){
        pestService.deletePest(pestId);
    }
}
