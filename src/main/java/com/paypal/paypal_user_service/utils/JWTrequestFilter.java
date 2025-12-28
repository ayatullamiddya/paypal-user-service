package com.paypal.paypal_user_service.utils;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class JWTrequestFilter extends OncePerRequestFilter {
    //private static Logger log = LogManager.getLogManager().getLogger(JWTrequestFilter.class.getName());

    private final JWTUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       // log.debug("JWT Filter triggered for URI: {}", request.getRequestURI());
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        String role = null;

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
            try{
                username = jwtUtil.extractUsername(jwt);
                role = jwtUtil.extractRole(jwt);
            }catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT Token expired");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                return;
            }
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                if(jwtUtil.validateToken(jwt)){
                    if(role == null || role.isBlank()){
                        filterChain.doFilter(request,response);
                        return;
                    }
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null, List.of(new SimpleGrantedAuthority("ROLE_"+role)));
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);


                    filterChain.doFilter(request,response);
                }


            }
        }else{
            filterChain.doFilter(request,response);
        }
    }
}
