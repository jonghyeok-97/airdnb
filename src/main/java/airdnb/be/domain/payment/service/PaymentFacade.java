package airdnb.be.domain.payment.service;

import airdnb.be.client.TossClient;
import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentFacade {

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
        TossPaymentConfirm tossPaymentConfirm = tossClient.confirmPayment(request.paymentKey(), request.orderId(),
                request.amount());

        try {
            // 결제 및 예약 INSERT 에 대한 트랜잭션 시작
            return paymentService.confirmReservation(tossPaymentConfirm, request.reservationId(), request.memberId());
        } catch (RuntimeException e) {
            // 결제 취소 요청 보내기
            tossClient.cancelPayment();

            log.warn("예약 실패 이유: {}", e);
            throw new BusinessException(ErrorCode.NOT_CONFIRM_RESERVATION);
        }
    }
}
