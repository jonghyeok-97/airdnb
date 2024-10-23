package airdnb.be.domain.payment.service.response;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;

public record PaymentConfirmResponse(

        Long tossPaymentConfirmId,
        String orderId,
        String orderName,
        String requestedAt

) {
    public static PaymentConfirmResponse from(TossPaymentConfirm tossPaymentConfirm) {
        return new PaymentConfirmResponse(
                tossPaymentConfirm.getTossPaymentConfirmId(),
                tossPaymentConfirm.getOrderId(),
                tossPaymentConfirm.getOrderName(),
                tossPaymentConfirm.getRequestedAt()
        );
    }
}
