package com.dataus.template.securitycomplex.common.utils;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;

    public void setData(String key, String value) {
        stringRedisTemplate.opsForValue()
            .set(key, value);
    }

    public void setDataExipre(String key, String value, Duration duration) {
        stringRedisTemplate.opsForValue()
            .set(key, value, duration);
    }

    public Optional<String> getData(String key) {
        return Optional.ofNullable(
                stringRedisTemplate
                    .opsForValue()
                    .get(key));
    }

    public Boolean deleteData(String key) {
        return stringRedisTemplate.delete(key);
    }
    
}
