package com.paypal.paypal_user_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class JWTUtil {

        private static final String SECRET = "secret1234secret1234secret1234secret1234";
    // generating signinkey using secret key.
        private Key getSigninKey(){
            return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        }

        // taking token, extracting the email
        public String extractEmail(String token){
            return Jwts.parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

    public Claims extractAllCLiams(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

        public boolean validateToken(String token){
            try{
                extractEmail(token);
                if(isTokenExpired(token))
                    return true;
                else
                    return false;
            }catch (Exception e){
                return false;
            }
        }

        public String extractUsername(String token){


            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            System.out.println("CLAIMS = " + claims);
            System.out.println("SUB = " + claims.getSubject());
            return claims
                    .getSubject();
        }

        public Date getExpiration(String token){
           return extractAllCLiams(token).getExpiration();
        }

        public boolean isTokenExpired(String token){
            try {
                Jwts.parserBuilder()
                        .setSigningKey(getSigninKey())
                        .build()
                        .parseClaimsJws(token); // auto validates exp
                return true;
            } catch (ExpiredJwtException e) {
                return false;
            }
        }

        public String generateToken(String username){
            Map<String, Object> claims = new HashMap<>();
            return createToken(claims,username);
        }

        public String createToken(Map<String,Object> claims, String username){
            return Jwts.builder()
                    .addClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis()+1000*60*5))
                    .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        public String extractRole(String token){
            return (String)Jwts.parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role");

        }
}
