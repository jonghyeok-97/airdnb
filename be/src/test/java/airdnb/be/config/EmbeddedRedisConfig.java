package airdnb.be.config;

import airdnb.be.IntegrationTestSupport;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig extends IntegrationTestSupport {

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    private RedisServer redisServer;


    @PostConstruct
    public void postConstruct() throws IOException {
        int port = isRedisRunning() ? findAvaliablePort() : this.port;
        this.redisServer = new RedisServer(port);
        redisServer.start();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisOperations(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private int findAvaliablePort() throws IOException{
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }
        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }

    private boolean isRedisRunning() throws IOException{
        return isRunning(executeGrepProcessCommand(port));
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
        }

        return StringUtils.hasLength(pidInfo.toString());
    }

    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
        String[] shell = {"cmd.exe", "/y", "/c", command};
        return Runtime.getRuntime().exec(shell);
    }
}
