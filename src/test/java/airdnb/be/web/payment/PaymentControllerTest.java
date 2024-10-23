package airdnb.be.web.payment;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.ControllerTestSupport;
import airdnb.be.domain.payment.service.response.PaymentConfirmResponse;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.web.payment.request.PaymentConfirmRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PaymentControllerTest extends ControllerTestSupport {

    @DisplayName("예약 결제 임시 데이터 추가하면 OK_200 을 응답한다.")
    @Test
    void addPaymentTemporaryData() throws Exception {
        // given
        Long reservationId = 1L;

        given(paymentService.addPaymentTemporaryData(any(), any(), any(), any(), any()))
                .willReturn(1L);

        // when then
        mockMvc.perform(
                        post("/payment/reservation/{reservationId}/request", reservationId)
                                .queryParam("paymentKey", "randomValue1")
                                .queryParam("amount", "30000")
                                .queryParam("orderId", "randomValue2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .sessionAttr(LOGIN_MEMBER, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());
    }

    @DisplayName("예약 결제 승인에 대해 OK_200 을 응답한다.")
    @Test
    void confirmPaymentByReservation() throws Exception {
        // given
        Long paymentTemporaryId = 1L;
        String paymentKey = "paymentKey_123xth43";
        String amount = "50000";
        String orderId = "orderId_12323xth43";

        PaymentConfirmRequest request = createPaymentConfirmRequest(paymentKey, amount, orderId);

        PaymentReservationResponse response = createPaymentReservationResponse(paymentKey, amount, orderId);
        given(paymentFacade.confirmPaymentByReservation(any()))
                .willReturn(response);

        // when then
        mockMvc.perform(
                        post("/payment/{paymentTemporaryId}/confirm", paymentTemporaryId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .sessionAttr(LOGIN_MEMBER, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());
    }

    private PaymentConfirmRequest createPaymentConfirmRequest(String paymentKey, String amount, String orderId) {
        return new PaymentConfirmRequest(
                1L,
                paymentKey,
                amount,
                orderId
        );
    }

    private PaymentReservationResponse createPaymentReservationResponse(String paymentKey, String amount, String orderId) {
        return new PaymentReservationResponse(
                new PaymentConfirmResponse(
                        1L,
                        orderId,
                        "orderName_예약 1건",
                        "requestedAt_결제요청시간"
                ),
                new ReservationResponse(
                        1L,
                        1L,
                        1L,
                        LocalDateTime.of(2024, 8, 13, 15, 0),
                        LocalDateTime.of(2024, 8, 15, 11, 0),
                        new BigDecimal(amount),
                        3
                ));
    }
}