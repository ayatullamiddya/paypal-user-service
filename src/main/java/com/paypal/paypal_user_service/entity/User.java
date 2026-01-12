package com.paypal.paypal_user_service.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paypal.paypal_user_service.enums.CountryCurrency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="app_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id",length = 5, nullable = false, unique = true)
    private String userId;
    private String name;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    @Column(nullable = false, length = 256)
    private String password;
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "currencyCode",nullable = false)
    private CountryCurrency countryCurrency;




}
