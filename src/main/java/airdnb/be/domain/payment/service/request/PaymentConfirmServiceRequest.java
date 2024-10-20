package airdnb.be.domain.payment.service.request;

public record PaymentConfirmServiceRequest(

        Long paymentTemporaryId,
        Long memberId,
        Long reservationId,
        String paymentKey,
        String amount,
        String orderId
) {
}
