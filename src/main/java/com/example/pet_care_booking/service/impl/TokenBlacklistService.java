package com.example.pet_care_booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String BLACK_PREFIX = "jwt:blacklist:";

    public void addToBlackList(String token, long remainingMillis) {
        redisTemplate.opsForValue()
                .set(BLACK_PREFIX + token,
                        true,
                        remainingMillis,
                        TimeUnit.SECONDS);


    }

    public boolean isBlacklisted(String token) {
        Boolean exist = redisTemplate.hasKey(
                BLACK_PREFIX + token
        );
        return Boolean.TRUE.equals(exist);
    }
}
