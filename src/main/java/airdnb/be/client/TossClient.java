package airdnb.be.client;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.toss.TossPaymentDto;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class TossClient {

    @Value("${payment.toss.api.secret}")
    private String secretApiKey;

    private final RestTemplate tossRestTemplate;

    public TossPaymentConfirm confirmPayment(String paymentKey, String orderId, String amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + getEncodedKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> httpEntity = new HttpEntity<>(toJsonPayload(paymentKey, orderId, amount), headers);

        return tossRestTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm",
                httpEntity,
                TossPaymentConfirm.class
        ).getBody();
    }

    public TossPaymentDto cancelPayment(String paymentKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + getEncodedKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> httpEntity = new HttpEntity<>(createCancelReason(), headers);
        return tossRestTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel",
                httpEntity,
                TossPaymentDto.class,
                paymentKey
        ).getBody();
    }

    private String toJsonPayload(String paymentKey, String orderId, String amount) {
        return String.format(
                "{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":\"%s\"}",
                paymentKey, orderId, amount
        );
    }

    private String getEncodedKey() {
        return Base64.getEncoder().encodeToString((secretApiKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    private String createCancelReason() {
        return "{\"cancelReason\":\"재고 부족\"}";
    }
}
