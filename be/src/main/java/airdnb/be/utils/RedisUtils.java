package airdnb.be.utils;

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
        String redisValue = redisTemplate.opsForValue().get(key);
        if (redisValue == null) {
            return false;
        }
        return redisValue.equals(value);
    }
}
