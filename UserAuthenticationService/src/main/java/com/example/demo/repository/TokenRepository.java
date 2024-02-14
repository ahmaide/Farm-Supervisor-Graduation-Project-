package com.example.demo.repository;

import com.example.demo.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token findByData(String data);
}
