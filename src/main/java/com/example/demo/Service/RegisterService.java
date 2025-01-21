package com.example.demo.Service;

import com.example.demo.DTO.RegisterRequestDTO;
import com.example.demo.Model.UserModel;

public interface RegisterService {
    void register(RegisterRequestDTO registerRequest);

    boolean checkEmail(String email);
}
