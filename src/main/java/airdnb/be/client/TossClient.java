package airdnb.be.client;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TossClient {

    private final Environment env;
    private final ObjectMapper objectMapper;

    public TossPaymentConfirm confirmPayment(String paymentKey, String orderId, String amount)
            throws IOException, InterruptedException {
        String secretApiKey = env.getProperty("payment.toss.api.secret");
        String encodedKey = Base64.getEncoder().encodeToString(secretApiKey.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                .header("Authorization", "Basic " + encodedKey)
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(toJsonPayload(paymentKey, orderId, amount)))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), TossPaymentConfirm.class);
    }

    private String toJsonPayload(String paymentKey, String orderId, String amount) {
        StringBuilder builder = new StringBuilder();
        builder.append("{")
                .append("\"paymentKey\":\"").append(paymentKey).append("\",")
                .append("\"orderId\":\"").append(orderId).append("\",")
                .append("\"amount\":\"").append(amount).append("\"")
                .append("}");

        return builder.toString();
    }
}
