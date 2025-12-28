package com.paypal.paypal_user_service.controller;


import com.paypal.paypal_user_service.dto.JwtTokenResponse;
import com.paypal.paypal_user_service.dto.LoginRequest;
import com.paypal.paypal_user_service.dto.SignupRequest;
import com.paypal.paypal_user_service.entity.User;
import com.paypal.paypal_user_service.repository.UserRepository;
import com.paypal.paypal_user_service.utils.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import java.util.Optional;

@RestController
@RequestMapping("/paypal/api/auth/v1")
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
        Optional<User> userExists = userRepository.findByEmail(signupRequest.getEmail());

        if(userExists.isPresent()){
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role("user")
                .build();

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not found");
        }

        User user = userOpt.get();

        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credential");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role",user.getRole());

        String token = jwtUtil.createToken(claims,user.getEmail());
        return ResponseEntity.ok().body(new JwtTokenResponse(token));

    }

}
