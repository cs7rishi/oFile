package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.model.request.LoginRequest;
import com.cs7rishi.oFile.model.request.RegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest){

        return "Registration Successfull";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){
        return  "Login Successful";
    }
}
