package com.gentle.springsecuritypractice.redis;

import java.util.concurrent.TimeUnit;

public interface RedisService {

    // 저장 (기본 TTL 없이 저장)
    void setValue(String key, String value);

    // 저장 (TTL 포함)
    void setValue(String key, String value, long timeout, TimeUnit timeUnit);

    // 조회
    String getValue(String key);

    // 삭제
    void deleteValue(String key);

    // 존재 여부 확인
    boolean hasKey(String key);

}
