package airdnb.be.client;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisClient {

    private final RedisTemplate<String, String> redisTemplate;

    public void addData(String key, String value, Long expiredTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expiredTime, timeUnit);
    }

    public void addData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean hasData(String key, String value) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(redisValue -> redisValue.equals(value))
                .orElse(false);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
