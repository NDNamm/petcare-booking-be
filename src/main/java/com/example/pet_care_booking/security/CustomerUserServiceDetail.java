package com.example.pet_care_booking.security;

import com.example.pet_care_booking.model.User;
import com.example.pet_care_booking.exception.AppException;
import com.example.pet_care_booking.exception.ErrorCode;
import com.example.pet_care_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerUserServiceDetail implements UserDetailsService {
   private final UserRepository userRepository;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user =  userRepository.findUserByUserName(username)
             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

      List<GrantedAuthority> authorities = List.of(
             new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
      );
      return new org.springframework.security.core.userdetails.User(
             user.getUserName(),
             user.getPassword(),
             authorities
      );
   }

}
