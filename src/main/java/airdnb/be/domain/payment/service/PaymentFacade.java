package airdnb.be.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentFacade {

    private final PaymentService paymentService;

    public Long addPaymentTemporaryData(Long memberId, Long reservationId, String paymentKey, String amount,
                                        String orderId) {
        return paymentService.addPaymentTemporaryData(memberId, reservationId, paymentKey, amount, orderId);
    }
}
