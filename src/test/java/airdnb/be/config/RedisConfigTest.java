package airdnb.be.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

class RedisConfigTest {

    @DisplayName("레디스 연결은 Lettuce 클라이언트를 사용한다")
    @Test
    void beanCheck() {
        // given
        RedisConfig redisConfig = new RedisConfig();
        RedisConnectionFactory redisConnectionFactory = redisConfig.redisConnectionFactory();

        // when then
        assertThat(redisConnectionFactory.getClass()).isEqualTo(LettuceConnectionFactory.class);
    }

    @DisplayName("레디스 연결이 되면 레디스 템플릿은 존재한다")
    @Test
    void test() {
        // given
        RedisConfig redisConfig = new RedisConfig();
        RedisConnectionFactory redisConnectionFactory = redisConfig.redisConnectionFactory();

        // when
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisOperations(redisConnectionFactory);

        // then
        assertThat(redisTemplate).isNotNull();
    }
}