package com.example.pet_care_booking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final long EXPIRATION_TIME_ACCESS = 24 * 60 * 60 * 1000;
    private static final long EXPIRATION_TIME_REFRESH = 7 * 24 * 60 * 60 * 1000;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(String userName, String role) {
        return Jwts.builder()
                .setSubject(userName)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_ACCESS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date isExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    public long getRemainingMillis(String token) {
        return isExpiration(token).getTime() - System.currentTimeMillis();
    }

    public String extractRole(String token) {
        return (String) parseToken(token).get("role");
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = userDetails.getUsername();
        return username.equals(extractUsername(token)) && !isExpiration(token).before(new Date());
    }
}
