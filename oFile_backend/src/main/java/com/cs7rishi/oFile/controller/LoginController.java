package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.model.request.LoginRequest;
import com.cs7rishi.oFile.model.request.RegisterRequest;
import com.cs7rishi.oFile.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        loginService.register(registerRequest);
        return "Registration Done";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return "Login Successful";
    }
}
