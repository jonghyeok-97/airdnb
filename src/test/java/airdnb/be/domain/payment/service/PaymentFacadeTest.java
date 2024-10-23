package airdnb.be.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.client.TossClient;
import airdnb.be.domain.payment.PaymentTemporaryRepository;
import airdnb.be.domain.payment.entity.PaymentTemporary;
import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.entity.TossPaymentStatus;
import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import airdnb.be.domain.payment.service.response.PaymentConfirmResponse;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

class PaymentFacadeTest extends IntegrationTestSupport {

    @Autowired
    private PaymentFacade paymentFacade;

    @MockBean
    private TossClient tossClient;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private PaymentTemporaryRepository paymentTemporaryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("결제 임시 데이터를 저장하면 결제 임시 데이터ID를 반환한다")
    @Test
    void addPaymentTemporaryData() {
        // given
        Long returnValue = 1L;
        given(paymentService.addPaymentTemporaryData(anyLong(), anyLong(), anyString(), anyString(), anyString()))
                .willReturn(returnValue);
        // when
        Long result = paymentFacade.addPaymentTemporaryData(1L, 1L, "paymentKey", "amount", "orderId");

        // then
        assertThat(result).isEqualTo(returnValue);
    }

    @Transactional
    @DisplayName("결제 승인을 받으면 결제승인과 예약에 대한 응답이 온다")
    @Test
    void confirmPaymentByReservation() throws IOException, InterruptedException {
        // given
        Reservation reservation = saveReservation(50000);
        PaymentTemporary paymentTemporary = savePaymentTemporary("orderId", "paymentKey", "50000");

        PaymentConfirmServiceRequest request = new PaymentConfirmServiceRequest(
                paymentTemporary.getPaymentTemporaryId(),
                1L,
                1L,
                "paymentKey",
                "50000",
                "orderId"
        );

        TossPaymentConfirm tossPaymentConfirm = TossPaymentConfirm.builder()
                .paymentKey("paymentKey")
                .orderId("orderId")
                .orderName("orderName")
                .mId("mId")
                .lastTransactionKey("lastTransactionKey")
                .taxExemptionAmount(50000)
                .status(TossPaymentStatus.DONE)
                .requestedAt("requestedAt")
                .build();
        given(tossClient.confirmPayment(anyString(), anyString(), anyString()))
                .willReturn(tossPaymentConfirm);

        // when
        PaymentReservationResponse response = paymentFacade.confirmPaymentByReservation(request);

        // then
        PaymentConfirmResponse paymentConfirmResponse = response.paymentConfirmResponse();
        ReservationResponse reservationResponse = response.reservationResponse();

        assertThat(paymentConfirmResponse.tossPaymentConfirmId()).isNotNull();
        assertThat(paymentConfirmResponse)
                .extracting("orderId", "orderName", "requestedAt")
                .containsExactly("orderId", "orderName", "requestedAt");

        assertThat(reservationResponse.reservationId()).isNotNull();
        assertThat(reservationResponse)
                .extracting("totalFee")
                .isEqualTo(new BigDecimal("50000"));
    }

    private Reservation saveReservation(int amount) {
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 8, 23, 15, 0),
                LocalDateTime.of(2024, 8, 25, 11, 0),
                3,
                new BigDecimal(amount)
        );
        return reservationRepository.save(reservation);
    }

    private PaymentTemporary savePaymentTemporary(String orderId, String paymentKey, String amount) {
        PaymentTemporary paymentTemporary = new PaymentTemporary(
                1L,
                1L,
                orderId,
                paymentKey,
                amount
        );
        return paymentTemporaryRepository.save(paymentTemporary);
    }
}