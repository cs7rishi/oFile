package com.cs7rishi.oFile.utils;


import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationUtils {

    public static String getUserEmail(){
        return (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
