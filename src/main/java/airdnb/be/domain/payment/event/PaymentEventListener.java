package airdnb.be.domain.payment.event;

import airdnb.be.client.TossClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final TossClient tossClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleRollback(PaymentEvent paymentEvent) {
        log.info("이벤트 리스너 시작");
        tossClient.cancelPayment(paymentEvent.getPaymentKey());
    }
}
