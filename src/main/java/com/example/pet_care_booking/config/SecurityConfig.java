package com.example.pet_care_booking.config;

import com.example.pet_care_booking.modal.User;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.security.JwtAuthenticationFilter;
import com.example.pet_care_booking.security.JwtUtils;
import com.example.pet_care_booking.security.RestAccessDeniedHandler;
import com.example.pet_care_booking.security.RestAuthenticationEntryPoint;
import com.example.pet_care_booking.service.impl.CustomOidcUserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomOidcUserService customOidcUserService, JwtUtils jwtUtils, UserRepository userRepository) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public APIs
                        .requestMatchers("/api/auth/**", "/api/payment/**", "/api/appointment/check-free-time","/api/appointment/check-phone", "/api/forgot-password", "/api/reset-password", "/api/appointment/add/**", "/api/product/product-details/*", "/api/product-review/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/product/**", "/api/category/**", "/api/order/history", "/api/appointment/history",
                                "/api/orderDetail/**", "/api/examination/**", "/api/vet/**")
                        .permitAll()
                        .requestMatchers("/api/cart/session/**", "/api/order/**", "/api/order/cancel/*").permitAll()
                        .requestMatchers("/api/auth/reset-password/*").hasAnyRole("ADMIN")
                        // Rating GET public, nhưng POST/PUT/DELETE cần login
                        .requestMatchers(HttpMethod.GET, "/api/rating/**").permitAll()
                        .requestMatchers("/api/rating/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/vnpay/**").permitAll()
                        // Admin only - more specific rules
                        .requestMatchers("/api/admin/**", "/api/category/").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER", "DOCTOR", "CUSTOMER")
                        .requestMatchers("/api/user/update-profile").hasAnyRole("ADMIN", "USER", "DOCTOR", "CUSTOMER")
                        // Any other API requests need authentication
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .userInfoEndpoint(userInfor -> userInfor.oidcUserService(customOidcUserService))
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");
                            User user = userRepository.findUserByEmail(email)
                                    .orElseThrow(() -> new IllegalArgumentException("Not found user"));
                            String token = jwtUtils.createAccessToken(user.getUserName(), user.getRole().getName());
                            String refreshToken = jwtUtils.createRefreshToken(user.getUserName());
                            Cookie cookie = new Cookie("refresh_token", refreshToken);
                            cookie.setHttpOnly(true);
                            cookie.setPath("/");
                            cookie.setSecure(false);
                            cookie.setMaxAge(7 * 24 * 60 * 60);
                            response.addCookie(cookie);
                            String redirectUrl = "http://localhost:5173/oauth2/success?token=" + token;
                            response.sendRedirect(redirectUrl);
                        }))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // 401 cho tất cả API
                        .accessDeniedHandler(restAccessDeniedHandler)           // 403 cho role không đủ
                )


                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
