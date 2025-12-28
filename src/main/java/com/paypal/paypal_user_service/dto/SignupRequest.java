package com.paypal.paypal_user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private  String email;
    private  String password;
    private String adminKey;
}
