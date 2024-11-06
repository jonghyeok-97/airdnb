package airdnb.be.config;

import airdnb.be.exception.TossPaymentErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.HttpClientErrorException;
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
                .errorHandler(new TossResponseErrorHandler())
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


    static class TossResponseErrorHandler implements org.springframework.web.client.ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().is5xxServerError() ||
                    response.getStatusCode().is4xxClientError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            if (response.getStatusCode().is4xxClientError()) {
                String body = new LoggingInterceptor().readBody(response);
                TossPaymentErrorResponse errorResponse = convertToTossPaymentErrorResponse(body);
                log.info("code={}, message={}", errorResponse.code(), errorResponse.message());

                throw new HttpClientErrorException(errorResponse.message(), response.getStatusCode(), errorResponse.code(), null, null, null);
            }
            if (response.getStatusCode().is5xxServerError()) {
                log.error("토스 페이먼츠 내부 서버 오류");
            }
        }

        private TossPaymentErrorResponse convertToTossPaymentErrorResponse(String body) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, TossPaymentErrorResponse.class);
        }
    }
}

