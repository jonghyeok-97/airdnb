package airdnb.be.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(value = TossClient.class)
class TossClientTest {

    @Autowired
    private TossClient tossClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockServer;

    @DisplayName("결제 승인 성공 시 토스페이먼츠API의 정상 JSON을 응답받는다")
    @Test
    void confirmPayment() throws JsonProcessingException {
        // given
        String paymentKey = "paymentKey";
        String amount = "승인 가격";
        String orderId = "주문ID";

        String expectResult = objectMapper.writeValueAsString(createTossPaymentConfirm(paymentKey, orderId));
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        TossPaymentConfirm tossPaymentConfirm = tossClient.confirmPayment(paymentKey, orderId, amount);

        // then
        assertThat(tossPaymentConfirm.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(tossPaymentConfirm.getOrderId()).isEqualTo(orderId);
    }

    private TossPaymentConfirm createTossPaymentConfirm(String paymentKey, String orderId) {
        return TossPaymentConfirm.builder()
                .paymentKey(paymentKey)
                .orderName("주문이름")
                .orderId(orderId)
                .requestedAt("승인요청시각")
                .build();
    }
}