package com.paypal.paypal_user_service.controller;


import com.paypal.paypal_user_service.client.WalletClient;
import com.paypal.paypal_user_service.dto.*;
import com.paypal.paypal_user_service.entity.User;
import com.paypal.paypal_user_service.enums.CountryCurrency;
import com.paypal.paypal_user_service.repository.UserRepository;
import com.paypal.paypal_user_service.service.UserServiceImpl;
import com.paypal.paypal_user_service.utils.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Slf4j
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final UserController userController;
    private final WalletClient walletClient;
    private final UserServiceImpl userService;

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

        for(CountryCurrency c : CountryCurrency.values()){
            if(signupRequest.getCountryName().equals(c.getCountryName())){
                user.setCountryCurrency(c);
                break;
            }
        }



        //userController.createUser(user);
        //User savedUser = userRepository.save(user);
        User savedUser = userService.createUser(user);
        try{
            CreateWalletRequest request = new CreateWalletRequest();
            request.setUserId(savedUser.getUserId());
            request.setCurrency("Dol");
            WalletResponse wallet = walletClient.createWallet(request);
            log.info("wallet is created");
            return ResponseEntity.ok("User registered successfully");
        }catch (Exception e){
            log.error("while creating wallet, error occured: "+e.getMessage());
            userRepository.deleteById(savedUser.getId());
            return ResponseEntity.badRequest().body("user not created, some technical issue, sorry for inconvenience");
        }

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
        claims.put("user_id",String.valueOf(user.getId()));

        String token = jwtUtil.createToken(claims,user.getEmail());
        return ResponseEntity.ok().body(new JwtTokenResponse(token));

    }
    @PostMapping("/admin/createuser")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody SignupRequest signupRequest){
        Optional<User> userExists = userRepository.findByEmail(signupRequest.getEmail());

        if(userExists.isPresent()){
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role("admin")
                .build();

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

}
