package airdnb.be.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.payment.entity.PaymentTemporary;
import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.entity.TossPaymentStatus;
import airdnb.be.domain.payment.service.PaymentService;
import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import airdnb.be.domain.payment.service.response.PaymentConfirmResponse;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.service.ReservationServiceV2;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private ReservationServiceV2 reservationServiceV2;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentTemporaryRepository paymentTemporaryRepository;

    @Autowired
    private TossPaymentConfirmRepository tossPaymentConfirmRepository;

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAllInBatch();
        paymentTemporaryRepository.deleteAllInBatch();
    }

    @DisplayName("결제 임시 데이터 저장중 결제하려는 총 금액이 맞지 않으면 예외가 발생한다.")
    @Test
    void addPaymentTemporaryDataIsFailByAmount() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );
        Reservation saved = reservationRepository.save(reservation);

        // when then
        assertThatThrownBy(() -> paymentService.addPaymentTemporaryData(1L, saved.getReservationId(),
                        "paymentKey", "40000", "orderId"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_EQUAL_AMOUNT);
    }

    @DisplayName("결제 임시 데이터를 저장한다")
    @Test
    void addPaymentTemporaryData() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );
        Reservation saved = reservationRepository.save(reservation);
        System.out.println("saved.hasTotalFee(\"30000\") = " + saved.hasTotalFee("30000"));

        // when
        Long temporaryId = paymentService.addPaymentTemporaryData(1L, saved.getReservationId(),
                "paymentKey", "30000", "orderId");

        // then
        int savedSize = paymentTemporaryRepository.findAll().size();
        assertThat(savedSize).isEqualTo(1);
        assertThat(temporaryId).isNotNull();
    }

    @DisplayName("결제 승인 전에 결제 임시 데이터와 비교한다")
    @Test
    void existsPaymentTemporary() {
        // given
        PaymentTemporary paymentTemporary = new PaymentTemporary(
                1L, 1L, "orderId", "paymentKey", "amount");
        PaymentTemporary saved = paymentTemporaryRepository.save(paymentTemporary);

        PaymentConfirmServiceRequest request = new PaymentConfirmServiceRequest(
                saved.getPaymentTemporaryId(),
                1L,
                1L,
                "paymentKey",
                "amount",
                "orderId"
        );

        // when then
        assertThatCode(() -> paymentService.validateExistingPaymentTemporary(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제 승인 전에 결제 임시 데이터가 없으면 예외가 발생한다")
    @Test
    void existsPaymentTemporaryWithFail() {
        // given
        PaymentConfirmServiceRequest request = new PaymentConfirmServiceRequest(
                1L,
                1L,
                1L,
                "paymentKey",
                "amount",
                "orderId"
        );

        // when then
        assertThatThrownBy(() -> paymentService.validateExistingPaymentTemporary(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_TEMPORARY_DATA);
    }

    @DisplayName("토스 결제 승인 과 예약ID와 회원ID 로 예약을 확정짓는다")
    @Test
    void confirmReservation() {
        // given
        TossPaymentConfirm tossPaymentConfirm = TossPaymentConfirm.builder()
                .paymentKey("결제 고유 키")
                .orderId("주문 번호")
                .orderName("주문 이름")
                .mId("상점 아이디")
                .taxExemptionAmount(50000)
                .status(TossPaymentStatus.READY)
                .requestedAt("결제 시간")
                .build();

        given(reservationServiceV2.confirmReservation(anyLong(), anyLong()))
                .willReturn(new ReservationResponse(
                    1L,
                    1L,
                    1L,
                    LocalDateTime.of(2024, 8, 13, 15, 0),
                    LocalDateTime.of(2024, 8, 15, 11, 0),
                    new BigDecimal(50000),
                    3
                ));

        // when
        PaymentReservationResponse response = paymentService.confirmReservation(tossPaymentConfirm, 1L, 1L);

        // then
        PaymentConfirmResponse paymentConfirmResponse = response.paymentConfirmResponse();
        ReservationResponse reservationResponse = response.reservationResponse();

        assertThat(paymentConfirmResponse.tossPaymentConfirmId()).isNotNull();
        assertThat(paymentConfirmResponse)
                .extracting("orderId", "orderName", "requestedAt")
                .containsExactly("주문 번호", "주문 이름", "결제 시간");

        assertThat(reservationResponse.reservationId()).isNotNull();
        assertThat(reservationResponse)
                .extracting("totalFee")
                .isEqualTo(new BigDecimal("50000"));
    }

    @DisplayName("예약을 확정지을 때, 예약 확정에서 런타임 예외가 생기면 결제 승인 테이블이 롤백된다")
    @Test
    void confirmReservationWithException() {
        // given
        TossPaymentConfirm tossPaymentConfirm = TossPaymentConfirm.builder()
                .paymentKey("결제 고유 키")
                .orderId("주문 번호")
                .orderName("주문 이름")
                .mId("상점 아이디")
                .taxExemptionAmount(50000)
                .status(TossPaymentStatus.READY)
                .requestedAt("결제 시간")
                .build();

        given(reservationServiceV2.confirmReservation(anyLong(), anyLong()))
                .willThrow(new RuntimeException());

        // when
        assertThatThrownBy(() -> paymentService.confirmReservation(tossPaymentConfirm, 1L, 1L))
                .isInstanceOf(RuntimeException.class);

        // then
        assertThat(tossPaymentConfirmRepository.findAll()).hasSize(0);
        assertThat(reservationRepository.findAll()).hasSize(0);
    }
}