package airdnb.be.client;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.IntegrationTestSupport;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

class RedisClientTest extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisClient redisClient;

    @DisplayName("레디스에 데이터를 추가한다")
    @Test
    void addData() {
        // given
        redisClient.addData("key", "value", 5L, TimeUnit.SECONDS);

        // when
        String result = redisTemplate.opsForValue().get("key");

        // then
        assertThat(result).isEqualTo("value");
    }

    @DisplayName("레디스에 키와 값이 있으면 True 를 반환한다")
    @Test
    void hasDataIsTrue() {
        // given
        redisClient.addData("123", "d@naver.com", 5L, TimeUnit.MINUTES);

        // when then
        assertThat(redisClient.hasData("123", "d@naver.com")).isTrue();
    }

    @DisplayName("레디스에 키와 값중 하나라도 없으면 False 를 반환한다")
    @ParameterizedTest
    @CsvSource(value = {"key1,value,false", "key,value1,false"})
    void hasDataIsFalse(String key, String value, boolean result) {
        // given
        redisClient.addData("key", "value", 5L, TimeUnit.MINUTES);

        // when
        boolean actual = redisClient.hasData(key, value);

        // then
        assertThat(actual).isEqualTo(result);
    }
}