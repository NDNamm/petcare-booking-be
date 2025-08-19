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
                  // Public APIs
                  .requestMatchers("/api/auth/**", "/api/payment/**", "/api/cart/**").permitAll()
                  .requestMatchers(HttpMethod.GET, "/api/product/**", "/api/category/**", "/api/order/history",
                        "/api/orderDetail/**")
                  .permitAll()
                  .requestMatchers("/api/cart/session/**", "/api/order/**").permitAll()

                  // Rating GET public, nhưng POST/PUT/DELETE cần login
                  .requestMatchers(HttpMethod.GET, "/api/rating/**").permitAll()
                  .requestMatchers("/api/rating/**").hasAnyRole("ADMIN", "USER")

                  // Admin only - more specific rules
                  .requestMatchers("/api/admin/**", "/api/category/").hasRole("ADMIN")
                  .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER")

                  // Any other API requests need authentication
                  .anyRequest().authenticated())

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      return httpSecurity.build();
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}
