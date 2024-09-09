package com.cs7rishi.oFile.service.impl;

import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.model.request.RegisterRequest;
import com.cs7rishi.oFile.repository.CustomerRepository;
import com.cs7rishi.oFile.service.LoginService;
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
    public void register(RegisterRequest registerRequest) {
        customerRepository.save(createUser(registerRequest));
    }

    @Override
    public void login() {

    }

    private Customer createUser(RegisterRequest request) {
        return Customer.builder()
            .email(request.getEmail())
            .pwd(passwordEncoder.encode(request.getPassword()))
            .role("ROLE_USER")
            .build();
    }
}
