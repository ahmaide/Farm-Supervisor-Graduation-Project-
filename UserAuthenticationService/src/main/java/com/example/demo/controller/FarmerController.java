package com.example.demo.controller;

import com.example.demo.models.Farmer;
import com.example.demo.service.FarmerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/farmer")
@CrossOrigin
@AllArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;
    @GetMapping("all")
    public List<Farmer> getAllFarmers(){
        return farmerService.getAllFarmers();
    }

    @PostMapping("add")
    public void addFarmers(@RequestBody Farmer farmer){
        farmerService.addFarmer(farmer);
    }

    @PutMapping("update")
    public void updateFarmer(@RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "firstName", required = false) String firstName,
                             @RequestParam(value = "lastName", required = false)String lastName,
                             @RequestParam(value = "mobileNumber",required = false)String mobile,
                             @RequestParam(value = "password",required = false)String password){
        farmerService.updateFarmer(email,firstName,lastName,mobile,password);
    }

    @DeleteMapping("delete/{farmerId}")
    public void deleteFarmer(@PathVariable("farmerId") long farmerId){
        farmerService.deleteFarmer(farmerId);
    }

    @GetMapping("emailcheck")
    public String checkEmail(@RequestParam("email") String email){
        return farmerService.checkEmail(email);
    }

    @GetMapping("logincheck")
    public String loginCheck(@RequestParam("email") String email, @RequestParam("password") String password){
        return farmerService.loginCheck(email, password);
    }

    @GetMapping("firstname")
    public String getFirstName(@RequestParam("email") String email){
        return farmerService.getFirstName(email);
    }
    @GetMapping("lastname")
    public String getLastName(@RequestParam("email") String email){
        return farmerService.getLastName(email);
    }
    @GetMapping("password")
    public String getPassword(@RequestParam("email") String email){
        return farmerService.getPassword(email);
    }
    @GetMapping("mobilenumber")
    public String getMobileNumber(@RequestParam("email") String email){
        return farmerService.getMobileNumber(email);
    }

    @GetMapping("fullName")
    public String getFullName(@RequestParam("email") String email){
            return farmerService.getFullName(email);
    }


}
