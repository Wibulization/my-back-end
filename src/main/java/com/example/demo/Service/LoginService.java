package com.example.demo.Service;

import java.util.Map;

import com.example.demo.DTO.LoginRequestDTO;

public interface LoginService {
    Map<String, String> getKeycloakToken(LoginRequestDTO loginRequest);

    String getUserInfo(String token);
}
