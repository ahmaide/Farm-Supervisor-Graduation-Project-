package com.example.demo.config;

import com.example.demo.service.JwtService;
import com.example.demo.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenExtractorFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";

    private static final String BEARER_HEADER = "Bearer ";

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final TokenService tokenService;

    @Getter
    private String username;

    @Getter
    private String jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HEADER);
        if (checkHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (!checkExistingUserDetails(username)) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (tokenValidByUser(userDetails)) {
                UsernamePasswordAuthenticationToken authToken = setAuthToken(userDetails);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkHeader(String header){
        return (header == null || !header.startsWith(BEARER_HEADER));
    }

    private boolean checkExistingUserDetails(String username){
        return (username == null || SecurityContextHolder.getContext().getAuthentication() != null);
    }

    private boolean tokenValidByUser(UserDetails userDetails){
        var isTokenValid = (!tokenService.findBothRevokeAndExpirationByData(jwt));
        return jwtService.isTokenValid(jwt, userDetails) && isTokenValid;
    }

    private UsernamePasswordAuthenticationToken setAuthToken(UserDetails userDetails){
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

}