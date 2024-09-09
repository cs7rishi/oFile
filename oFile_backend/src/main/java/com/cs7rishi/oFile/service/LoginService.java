package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.model.request.RegisterRequest;

public interface LoginService {
    void register(RegisterRequest registerRequest);
    void login();
}
