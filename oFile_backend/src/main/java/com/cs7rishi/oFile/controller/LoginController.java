package com.cs7rishi.oFile.controller;

import com.cs7rishi.oFile.constants.ResponseConstant;
import com.cs7rishi.oFile.model.request.RegisterRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;
import com.cs7rishi.oFile.service.LoginService;
import com.cs7rishi.oFile.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/public")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse<?> register(@RequestBody RegisterRequest registerRequest) {
        return loginService.register(registerRequest);
    }
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse<?> login() {
        return ApiResponseUtil.success(null, ResponseConstant.LOGIN_SUCCESSFUL,
            ResponseConstant.EMPTY);
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericResponse<?> healthCheck() {
        return ApiResponseUtil.success(null, "OK", "Healthy");
    }

}
