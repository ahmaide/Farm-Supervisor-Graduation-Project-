package com.example.demo.config;

import com.example.demo.service.HttpService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenExtractorFilter jwtAuthFilter;

    private final HttpService httpService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        httpService.setPermissions(http);
        httpService.setLogoutRequest(http);
        httpService.addExtraConfigurations(http, jwtAuthFilter);
        return http.build();
    }
}