package airdnb.be.client;

import airdnb.be.IntegrationTestSupport;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

class RedisClientTest extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisClient redisClient;

    @DisplayName("레디스")
    @Test
    void test() {
        // given
        redisTemplate.opsForValue().set("124356", "1");
        // when

        Boolean b = redisTemplate.hasKey("124356");
        Assertions.assertThat(b).isTrue();
        // then

    }

    @DisplayName("")
    @Test
    void test1() {
        // given
        redisClient.addData("123", "d@naver.com", 5L, TimeUnit.MINUTES);
        // when
        Assertions.assertThat(redisClient.hasData("123", "d@naver.com")).isTrue();
        // then

    }
}