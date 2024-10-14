package airdnb.be.config;

import airdnb.be.IntegrationTestSupport;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig extends IntegrationTestSupport {

    private final int port = 6379;

    private RedisServer redisServer;

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

    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
        String[] shell = {"cmd.exe", "/y", "/c", command};
        return Runtime.getRuntime().exec(shell);
    }
}
