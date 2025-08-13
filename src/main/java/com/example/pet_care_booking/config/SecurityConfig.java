package com.example.pet_care_booking.config;


import com.example.pet_care_booking.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

   private final JwtAuthenticationFilter jwtAuthenticationFilter;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
      httpSecurity
             .cors(withDefaults())
             .csrf(csrf -> csrf.disable())
             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
             .authorizeHttpRequests(auth -> auth
                    // Cho phép login và các API công khai
                    .requestMatchers("/api/auth/**", "/api/payment/**", "/api/cart/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/category/**", "/api/product/**", "/api/rating/**",
                           "/api/order/history", "/api/orderDetail/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/cart/session/**", "/api/order/**").permitAll()

                    // Các quyền cần role
                    .requestMatchers("/api/rating/**").hasAnyRole("ADMIN", "USER")
                    .requestMatchers("/api/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
             )
             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      return httpSecurity.build();
   }


   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}
