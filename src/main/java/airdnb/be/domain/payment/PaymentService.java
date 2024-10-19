package airdnb.be.domain.payment;

import airdnb.be.domain.payment.entity.PaymentTemporary;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentTemporaryRepository paymentTemporaryRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void addPaymentTemporaryData(Long memberId, Long reservationId, String paymentKey, String amount,
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
        paymentTemporaryRepository.save(paymentTemporary);
    }
}
