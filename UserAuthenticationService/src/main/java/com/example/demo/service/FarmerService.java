package com.example.demo.service;

import com.example.demo.models.Crop;
import com.example.demo.models.Farmer;
import com.example.demo.models.Land;
import com.example.demo.repository.CropRespository;
import com.example.demo.repository.FarmerRepository;
import com.example.demo.repository.LandRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;


    private final PasswordEncoder passwordEncoder;
    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    public void addFarmer(Farmer farmer) {
        Optional<Farmer>farmerOptional =  farmerRepository.findById(farmer.getFarmerId());
        if(!farmerOptional.isPresent()){
            farmer.setPassword(passwordEncoder.encode(farmer.getPassword()));
            farmerRepository.save(farmer);
        }
        else{
            throw new IllegalStateException("Farmer already exist");
        }
    }
    @Transactional
    public void updateFarmer( String email, String firstName, String lastName, String mobile,String password) {

        long farmerId = farmerRepository.findIdByEmail(email);
        Farmer oldFarmer =  farmerRepository.findById(farmerId).orElseThrow(
                () -> new IllegalStateException("Farmer you want to update does not exist!")
        );
        if( firstName.length() > 0 &&
                !Objects.equals(firstName , oldFarmer.getFirstName())){
            oldFarmer.setFirstName(firstName);
        }
        if(firstName == null){
            oldFarmer.setFirstName(oldFarmer.getFirstName());
        }
        if(lastName!= null && lastName.length() > 0 &&
                !Objects.equals(lastName , oldFarmer.getLastName())){
            oldFarmer.setLastName(lastName);
        }
        if(lastName == null){
            oldFarmer.setLastName(oldFarmer.getLastName());
        }
        if(password!= null && password.length() > 0 &&
                !passwordEncoder.matches(password, oldFarmer.getPassword())){
            oldFarmer.setPassword(passwordEncoder.encode(password));
        }
        if(password == null){
            oldFarmer.setPassword(oldFarmer.getPassword());
        }
        if(mobile!= null && mobile.length() > 0 &&
                !Objects.equals(mobile , oldFarmer.getMobileNumber())){
            oldFarmer.setMobileNumber(mobile);
        }
        if(mobile == null){
            oldFarmer.setMobileNumber(oldFarmer.getMobileNumber());
        }
        if(email!= null && email.length() > 0 &&
                !Objects.equals(email , oldFarmer.getEmail())){
            oldFarmer.setEmail(email);
        }
        if(email == null){
            oldFarmer.setEmail(oldFarmer.getEmail());
        }

    }

    @Transactional
    public void deleteFarmer(long farmerId) {
        Optional<Farmer>farmerOptional =  farmerRepository.findById(farmerId);
        if(farmerOptional.isPresent()){
            farmerRepository.deleteById(farmerId);
        }else{
            throw new IllegalStateException("Farmer does not exist");
        }

    }


    public String checkEmail(String email) {

        boolean isExist = farmerRepository.existsByEmail(email);
        if(!isExist){
            return "available";
        }
        return "taken";

    }

    public String loginCheck(String email, String password){
        String isExist = this.checkEmail(email);

        if(isExist.compareTo("available") == 0){
            return "not registered";
        }
        else{
            String returnedPassword = farmerRepository.getPasswordByEmail(email);
            if(passwordEncoder.matches(password, returnedPassword)){
                return "correct password";
            }
            else{
                return "wrong password";
            }
        }

    }

    public String getFirstName(String email) {
        return farmerRepository.findFirstNameByEmail(email);
    }

    public String getLastName(String email) {
        return farmerRepository.findLastNameByEmail(email);
    }

    public String getPassword(String email) {
        return farmerRepository.getPasswordByEmail(email);
    }
    public String getMobileNumber(String email) {
        return farmerRepository.findMobileNumberByEmail(email);
    }

    public String getFullName(String email) {
        String firstName = farmerRepository.findFirstNameByEmail(email);
        String lastName = farmerRepository.findLastNameByEmail(email);
        return firstName + " " + lastName;
    }

    public Farmer save(Farmer farmer){
        return farmerRepository.save(farmer);
    }

    public Farmer findByEmail(String email){
        return farmerRepository.findByEmail(email);
    }

}
