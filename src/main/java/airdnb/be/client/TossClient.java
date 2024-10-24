package airdnb.be.client;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class TossClient {

    @Value("${payment.toss.api.secret}")
    private String secretApiKey;

    private final RestTemplate restTemplate;

    public TossClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public TossPaymentConfirm confirmPayment(String paymentKey, String orderId, String amount) {
        String encodedKey = Base64.getEncoder().encodeToString(secretApiKey.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> httpEntity = new HttpEntity<>(toJsonPayload(paymentKey, orderId, amount), headers);

        return restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm",
                httpEntity,
                TossPaymentConfirm.class
        ).getBody();
    }

    private String toJsonPayload(String paymentKey, String orderId, String amount) {
        return String.format(
                "{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":\"%s\"}",
                paymentKey, orderId, amount
        );
    }
}
