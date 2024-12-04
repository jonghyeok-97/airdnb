package airdnb.be.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Slf4j
@TestConfiguration
public class EmbeddedRedisConfig {

    private final int port = 6379;

    private RedisServer redisServer;

    /**
     * 병렬로 테스트를 실행하게 되어 여러 레디스가 띄워질 때, 주석을 해제하기
     */
    @PostConstruct
    public void postConstruct() throws IOException {
        int port = isRedisRunning() ? findAvaliablePort() : this.port;
        this.redisServer = new RedisServer(port);
        redisServer.start();
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
        return isRunning(executeGrepProcessCommand(this.port));
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {}

        return StringUtils.hasLength(pidInfo.toString());
    }

    // mac, linux
    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
        String[] shell = {"/bin/sh", "-c", command};
        return Runtime.getRuntime().exec(shell);
    }

    // window
//    private Process executeGrepProcessCommand(int port) throws IOException {
//        String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
//        String[] shell = {"cmd.exe", "/y", "/c", command};
//        return Runtime.getRuntime().exec(shell);
//    }
}
