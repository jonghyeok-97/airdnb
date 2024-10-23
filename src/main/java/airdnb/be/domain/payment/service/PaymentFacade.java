package airdnb.be.domain.payment.service;

import airdnb.be.client.TossClient;
import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.domain.payment.service.response.PaymentConfirmResponse;
import airdnb.be.domain.reservation.service.ReservationService;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentFacade {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final TossClient tossClient;

    public Long addPaymentTemporaryData(Long memberId, Long reservationId, String paymentKey, String amount,
                                        String orderId) {
        return paymentService.addPaymentTemporaryData(memberId, reservationId, paymentKey, amount, orderId);
    }

    public PaymentReservationResponse confirmPaymentByReservation(PaymentConfirmServiceRequest request) {
        // 임시 결제 데이터 존재하는지 확인
        paymentService.validateExistingPaymentTemporary(request);

        // 결제 승인 요청(네트워크)
        TossPaymentConfirm tossPaymentConfirm;
        try {
            tossPaymentConfirm = tossClient.confirmPayment(request.paymentKey(), request.orderId(),
                    request.amount());
        } catch (IOException | InterruptedException e) {
            // 결제 금액 부족 & 예약 동시성 실패 시 -> 결제 취소 요청
            throw new RuntimeException(e);
        }

        // 결제 승인 INSERT
        PaymentConfirmResponse paymentConfirmResponse = paymentService.addTossPaymentConfirm(tossPaymentConfirm);

        // 예약 확정 INSERT
        ReservationResponse reservationResponse = reservationService.reserveV2(request.reservationId(), request.memberId());

        return PaymentReservationResponse.of(paymentConfirmResponse, reservationResponse);
    }
}
