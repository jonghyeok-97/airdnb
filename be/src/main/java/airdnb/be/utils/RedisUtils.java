package airdnb.be.utils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate redisTemplate;

    public void addData(String key, String value, Long expiredTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expiredTime, timeUnit);
    }

    public boolean hasData(String key, String value) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(redisValue -> redisValue.equals(value))
                .orElse(false);
    }
}
