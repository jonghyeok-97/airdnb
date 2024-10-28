package airdnb.be.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import airdnb.be.domain.payment.entity.TossCancel;
import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.entity.TossPaymentStatus;
import airdnb.be.domain.payment.toss.TossPaymentDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

    @DisplayName("결제 취소 요청시 정상 JSON을 응답받는다")
    @Test
    void cancelPayment() throws JsonProcessingException {
        // given
        String paymentKey = "paymentKey";

        String expectResult = objectMapper.writeValueAsString(createTossPaymentDto());
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        TossPaymentDto tossPaymentDto = tossClient.cancelPayment(paymentKey);

        // then
        assertThat(tossPaymentDto.cancels()).isNotEmpty();
    }

    private TossPaymentConfirm createTossPaymentConfirm(String paymentKey, String orderId) {
        return TossPaymentConfirm.builder()
                .paymentKey(paymentKey)
                .orderName("주문이름")
                .orderId(orderId)
                .requestedAt("승인요청시각")
                .build();
    }

    private TossPaymentDto createTossPaymentDto() {
        return new TossPaymentDto(
                "paymentKey",
                "주문ID",
                "주문이름",
                "Merchant ID",
                "마지막 거래 키",
                50000,
                TossPaymentStatus.DONE,
                "요청 시각",
                List.of(TossCancel.builder()
                        .transactionKey("거래 키")
                        .cancelReason("취소 이유")
                        .taxExemptionAmount(5000)
                        .cancelStatus("취소 상태")
                        .build())
        );
    }
}