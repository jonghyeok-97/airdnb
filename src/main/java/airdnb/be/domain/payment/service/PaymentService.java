package airdnb.be.domain.payment.service;

import airdnb.be.domain.payment.PaymentTemporaryRepository;
import airdnb.be.domain.payment.TossPaymentConfirmRepository;
import airdnb.be.domain.payment.entity.PaymentTemporary;
import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import airdnb.be.domain.payment.event.PaymentEvent;
import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import airdnb.be.domain.payment.service.response.PaymentConfirmResponse;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.service.ReservationServiceV2;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentTemporaryRepository paymentTemporaryRepository;
    private final ReservationRepository reservationRepository;
    private final TossPaymentConfirmRepository tossPaymentConfirmRepository;
    private final ReservationServiceV2 reservationServiceV2;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long addPaymentTemporaryData(Long memberId, Long reservationId, String paymentKey, String amount,
                                        String orderId) {
        // 예약이 존재하는지 확인
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESERVATION));

        // 예약의 총 금액이 맞는지 확인
        if (!reservation.hasTotalFee(amount)) {
            throw new BusinessException(ErrorCode.NOT_EQUAL_AMOUNT);
        }

        // 결제 임시 데이터 저장
        PaymentTemporary paymentTemporary = new PaymentTemporary(memberId, reservationId, orderId, paymentKey, amount);
        return paymentTemporaryRepository.save(paymentTemporary).getPaymentTemporaryId();
    }

    public void validateExistingPaymentTemporary(PaymentConfirmServiceRequest request) {
        paymentTemporaryRepository.findById(request.paymentTemporaryId())
                .filter(temp -> temp.equals(request.toPaymentTemporary()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_TEMPORARY_DATA));
    }

    @Transactional
    public PaymentReservationResponse confirmReservation(
            TossPaymentConfirm tossPaymentConfirm, PaymentConfirmServiceRequest request) {
        // 이벤트 발행
        eventPublisher.publishEvent(new PaymentEvent(request.paymentKey()));

        // 결제 승인 INSERT
        TossPaymentConfirm saved = tossPaymentConfirmRepository.save(tossPaymentConfirm);
        PaymentConfirmResponse paymentConfirmResponse = PaymentConfirmResponse.from(saved);

        // 예약 확정 INSERT
        ReservationResponse reservationResponse = reservationServiceV2.confirmReservation(request.reservationId(), request.memberId());

        return PaymentReservationResponse.of(paymentConfirmResponse, reservationResponse);
    }
}
