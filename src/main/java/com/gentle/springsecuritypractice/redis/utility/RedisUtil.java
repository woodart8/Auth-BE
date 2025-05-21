package com.gentle.springsecuritypractice.redis.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 저장 (기본 TTL 없이 저장)
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 저장 (TTL 포함)
    public void setValue(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    // 조회
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 삭제
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    // 존재 여부 확인
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
