package com.example.demo.service;

import com.example.demo.auth.AuthenticationRequest;
import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.auth.RegisterRequest;
import com.example.demo.config.TokenExtractorFilter;
import com.example.demo.models.Token;
import com.example.demo.models.Farmer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final FarmerService farmerService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TokenExtractorFilter tokenExtractorFilter;

    public Farmer userFromRequest(AuthenticationRequest request){
        Farmer farmer;
        if(request instanceof RegisterRequest registerRequest){
            farmer = Farmer.builder()
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .mobileNumber(registerRequest.getMobileNumber())
                    .build();
            farmerService.save(farmer);
        }
        else{
            farmer = farmerService.findByEmail(request.getEmail());
            if(farmer == null){
                throw new UsernameNotFoundException("Farmer not found");
            }
        }
        return farmer;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var farmer = userFromRequest(request);
        var token = tokenService.generateToken(farmer);
        return generateResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Farmer farmer = userFromRequest(request);
        //checkPassword(request, farmer);
        var token = tokenService.generateToken(farmer);
        System.out.println("Username: " + farmer.getEmail());
        System.out.println("Token: " + token);
        return generateResponse(token);
    }

    public void revokeToken(Token token){
        token.setExpired(true);
        token.setRevoked(true);
        tokenService.save(token);
    }

    public Token getCurrentToken(){
        String tokenData = tokenExtractorFilter.getJwt();
        return tokenService.findByData(tokenData);
    }

    private AuthenticationResponse generateResponse(Token token){
        return AuthenticationResponse
                .builder()
                .token(token.getData())
                .build();
    }

    private void checkPassword(AuthenticationRequest request, Farmer farmer){
        boolean isMatch = passwordEncoder.matches(request.getPassword(), farmer.getPassword());
        if(!isMatch){
            throw new BadCredentialsException("Invalid password");
        }
    }
}