package airdnb.be.domain.payment.event;

import lombok.Getter;

@Getter
public class PaymentEvent {

    private final String paymentKey;

    public PaymentEvent(String paymentKey) {
        this.paymentKey = paymentKey;
    }
}
