package com.assesment.maybank.spring_be.service;

import com.assesment.maybank.spring_be.dto.LoginDto;
import com.assesment.maybank.spring_be.dto.LoginRequest;
import com.assesment.maybank.spring_be.dto.RegisterRequest;

public interface AuthService {

    LoginDto login(LoginRequest request);

    void register(RegisterRequest request);

}
