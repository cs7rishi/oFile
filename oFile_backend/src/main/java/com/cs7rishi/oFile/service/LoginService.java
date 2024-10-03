package com.cs7rishi.oFile.service;

import com.cs7rishi.oFile.model.request.RegisterRequest;
import com.cs7rishi.oFile.model.response.GenericResponse;

public interface LoginService {
    GenericResponse<?> register(RegisterRequest registerRequest);
}
