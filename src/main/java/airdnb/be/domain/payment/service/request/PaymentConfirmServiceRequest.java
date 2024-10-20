package airdnb.be.domain.payment.service.request;

import airdnb.be.domain.payment.entity.PaymentTemporary;

public record PaymentConfirmServiceRequest(

        Long paymentTemporaryId,
        Long memberId,
        Long reservationId,
        String paymentKey,
        String amount,
        String orderId
) {
    public PaymentTemporary toPaymentTemporary() {
        return new PaymentTemporary(
                paymentTemporaryId,
                memberId,
                reservationId,
                orderId,
                paymentKey,
                amount
        );
    }
}
