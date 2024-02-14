package com.example.demo.repository;

import com.example.demo.models.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    boolean existsByEmail(String email);

    @Query(
            value = "select f.farmer_id from farmer f where f.email=?1",
            nativeQuery = true
    )
    long findIdByEmail(String email);

    @Query(
            value = "select f.password from farmer f where f.email=?1",
            nativeQuery = true
    )
    String getPasswordByEmail(String email);

    @Query(
            value = "select f.first_name from farmer f where f.email=?1",
            nativeQuery = true
    )
    String findFirstNameByEmail(String email);
    @Query(
            value = "select f.last_name from farmer f where f.email=?1",
            nativeQuery = true
    )
    String findLastNameByEmail(String email);
    @Query(
            value = "select f.mobile_number from farmer f where f.email=?1",
            nativeQuery = true
    )
    String findMobileNumberByEmail(String email);

    Farmer findByEmail(String email);

}
