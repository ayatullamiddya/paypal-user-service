package com.paypal.paypal_user_service.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;


public class ShortIdGenerator {
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int LENGTH = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateId(){
        StringBuilder sb = new StringBuilder(LENGTH);

        for(int i = 0; i < LENGTH; i++){
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}
