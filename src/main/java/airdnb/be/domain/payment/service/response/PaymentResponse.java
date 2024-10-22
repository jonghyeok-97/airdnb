package airdnb.be.domain.payment.service.response;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;

public record PaymentResponse(

        Long tossPaymentConfirmId,
        String orderId,
        String orderName,
        String requestedAt

) {
    public static PaymentResponse from(TossPaymentConfirm tossPaymentConfirm) {
        return new PaymentResponse(
                tossPaymentConfirm.getTossPaymentConfirmId(),
                tossPaymentConfirm.getOrderId(),
                tossPaymentConfirm.getOrderName(),
                tossPaymentConfirm.getRequestedAt()
        );
    }
}
