package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.constants.ResponseConstant;
import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.model.request.RegisterRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.service.LoginService;
import com.cs7rishi.oFile.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public GenericResponse<?> register(RegisterRequest registerRequest) {
        customerRepository.save(createUser(registerRequest));
        return ApiResponseUtil
            .success(null, ResponseConstant.REGISTER_SUCCESSFUL,ResponseConstant.EMPTY);
    }
    private Customer createUser(RegisterRequest request) {
        return Customer.builder()
            .email(request.getEmail())
            .pwd(passwordEncoder.encode(request.getPassword()))
            .role("ROLE_USER")
            .build();
    }
}
