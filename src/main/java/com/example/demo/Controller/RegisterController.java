package com.example.demo.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.DTO.RegisterRequestDTO;
import com.example.demo.Service.RegisterService;

import jakarta.validation.Valid;

@Controller
@CrossOrigin
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Valid RegisterRequestDTO registerRequest) {
        Map<String, String> response = new HashMap<>();

        if (registerService.checkEmail(registerRequest.getEmail())) {

            response.put("message", "Email đã tồn tại");
            return ResponseEntity.badRequest().body(response);

        }
        registerService.register(registerRequest);

        response.put("message", "Đăng ký thành công!");
        return ResponseEntity.ok().body(response);
    }
}
