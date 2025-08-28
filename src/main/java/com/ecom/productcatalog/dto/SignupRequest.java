package com.ecom.productcatalog.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
}