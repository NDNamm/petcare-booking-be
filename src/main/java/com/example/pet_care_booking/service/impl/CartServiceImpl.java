package com.example.pet_care_booking.service.impl;


import com.example.pet_care_booking.repository.CartRepository;
import com.example.pet_care_booking.repository.UserRepository;
import com.example.pet_care_booking.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

   private final CartRepository cartRepository;
   private final UserRepository userRepository;


}
