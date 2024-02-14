package com.example.demo.service;

import com.example.demo.models.Token;
import com.example.demo.models.Farmer;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String BEARER_TOKEN = "BEARER";

    private final TokenRepository tokenRepository;

    private final JwtService jwtService;

    public Token findByData(String tokenData){
        return tokenRepository.findByData(tokenData);
    }

    public boolean findExpirationByData(String token){
        return tokenRepository.findByData(token).isExpired();
    }

    public boolean findRevokedByData(String token){
        return tokenRepository.findByData(token).isRevoked();
    }

    public boolean findBothRevokeAndExpirationByData(String token){
        return findExpirationByData(token) || findRevokedByData(token);
    }

    public void save(Token token){
        tokenRepository.save(token);
    }

    public Token generateToken(Farmer user){
        var jwtToken = jwtService.buildToken(new HashMap<>(), user);
        var token = Token.builder()
                .userId(user.getFarmerId())
                .data(jwtToken)
                .type(BEARER_TOKEN)
                .expired(false)
                .revoked(false).build();
        save(token);
        return token;
    }

}
