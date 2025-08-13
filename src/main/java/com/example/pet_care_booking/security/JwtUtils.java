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
   private static final long EXPIRATION_TIME_ACCESS = 15 * 60 * 1000;
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

   public Claims parseToken(String token) {
      return Jwts.parserBuilder()
             .setSigningKey(key)
             .build()
             .parseClaimsJws(token)
             .getBody();
   }

   public String extractUsername(String token) {
      return parseToken(token).getSubject();
   }

   public String extractRole(String token) {
      return (String) parseToken(token).get("role");
   }

   public boolean validateToken(String token, UserDetails userDetails) {
      try{
         Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
         return true;
      }
      catch (Exception e) {
         return false;
      }
   }
}
