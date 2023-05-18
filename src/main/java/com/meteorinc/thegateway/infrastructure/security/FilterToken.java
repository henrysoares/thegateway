package com.meteorinc.thegateway.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteorinc.thegateway.application.user.TokenService;
import com.meteorinc.thegateway.domain.user.UserRepository;
import com.meteorinc.thegateway.infrastructure.rest.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FilterToken extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String token;

            var authorizationHeader = request.getHeader("Authorization");

            if(authorizationHeader != null){
                token = authorizationHeader.replace("Bearer ","");
                var subject = tokenService.getEmailOnToken(token);

                var user = userRepository.findByEmail(subject).get();

                var authentication = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (Exception exception){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .reason("O login expirou, tente logar novamente.")
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String errorMessage = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(errorMessage);
            return;
        }

        filterChain.doFilter(request,response);
    }
}
