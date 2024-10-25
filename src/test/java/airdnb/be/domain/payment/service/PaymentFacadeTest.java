package airdnb.be.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

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
import com.fasterxml.jackson.core.JsonProcessingException;
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
        saveReservation(50000);

        // when
        Long result = paymentFacade.addPaymentTemporaryData(1L, 1L, "paymentKey", "50000", "orderId");

        // then
        assertThat(result).isEqualTo(returnValue);
    }

    @Transactional
    @DisplayName("결제 승인을 받으면 결제승인과 예약에 대한 응답이 온다")
    @Test
    void confirmPaymentByReservation() throws JsonProcessingException {
        // given
        Reservation reservation = saveReservation(50000);
        PaymentTemporary paymentTemporary = savePaymentTemporary("orderId", "paymentKey", "50000");

        PaymentConfirmServiceRequest request = createPaymentConfirmServiceRequest(paymentTemporary.getPaymentTemporaryId());

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

    @Transactional
    @DisplayName("결제 및 예약 트랜잭션이 실패하면 결제 취소요청을 보낸다")
    @Test
    void confirmPaymentByReservation1() throws JsonProcessingException {
        // given
        PaymentConfirmServiceRequest request = createPaymentConfirmServiceRequest(1L);

        given(paymentService.confirmReservation(any(), anyLong(), anyLong()))
                .willThrow(RuntimeException.class);
        willDoNothing()
                .given(paymentService)
                .validateExistingPaymentTemporary(any());

        // when
        assertThatThrownBy(() -> paymentFacade.confirmPaymentByReservation(request))
                .isInstanceOf(RuntimeException.class);

        // then
        verify(tossClient, times(1)).cancelPayment();
    }

    private PaymentConfirmServiceRequest createPaymentConfirmServiceRequest(Long paymentTemporaryId) {
        return new PaymentConfirmServiceRequest(
                paymentTemporaryId,
                1L,
                1L,
                "paymentKey",
                "50000",
                "orderId"
        );
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