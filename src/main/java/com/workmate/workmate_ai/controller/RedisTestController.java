package com.workmate.workmate_ai.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/test")
    public String testRedis() {

        redisTemplate.opsForValue().set("workmate:test", "Redis is working!");

        String value = redisTemplate.opsForValue().get("workmate:test");

        return value;
    }
}