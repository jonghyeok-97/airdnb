package airdnb.be.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class TossRestTemplateConfig {

    @Bean
    public RestTemplate tossRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .additionalInterceptors(new LoggingInterceptor())
                .build();
    }

    @Slf4j
    static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            if (!log.isDebugEnabled()) {
                return execution.execute(request, body);
            }
            String sessionNumber = UUID.randomUUID().toString().substring(0, 6);
            printRequest(sessionNumber, request, body);
            ClientHttpResponse response = execution.execute(request, body);
            printResponse(sessionNumber, response);
            return response;
        }

        private void printRequest(String sessionNumber, HttpRequest req, byte[] body) {
            log.info("[{}] URI:{}, Method:{}, Headers:{} Body:{}",
                    sessionNumber, req.getURI(), req.getMethod(), req.getHeaders(),
                    new String(body, StandardCharsets.UTF_8));
        }

        private void printResponse(String sessionNumber, ClientHttpResponse response) throws IOException {
            log.info("[{}] Status:{}, Headers:{}, Body:{}",
                    sessionNumber, response.getStatusCode(), response.getHeaders(), readBody(response));
        }

        public String readBody(ClientHttpResponse response) throws IOException {
            return new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }
}

