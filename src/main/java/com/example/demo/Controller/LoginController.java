package com.example.demo.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.LoginRequestDTO;
import com.example.demo.Service.LoginService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequestDTO loginRequest) {

        // Get username when authenticate
        Map<String, String> response = loginService.getKeycloakToken(loginRequest);
        if (response != null) {
            String username = loginService.getUserInfo(response.get("access_token"));
            response.put("username", username);
            return ResponseEntity.status(200).body(response);
        }
        response.put("message", "Email hoặc mật khẩu không hợp lệ");
        return ResponseEntity.status(401).body(response);
    }

}
