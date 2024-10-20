package airdnb.be.web.payment.request;

import airdnb.be.domain.payment.service.request.PaymentConfirmServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentConfirmRequest(

        @NotNull(message = "결제 승인할 예약ID를 입력하세요.")
        Long reservationId,

        @NotBlank(message = "결제 승인한 결제키를 입력하세요.")
        String paymentKey,

        @NotBlank(message = "결제 승인할 금액을 입력하세요.")
        String amount,

        @NotBlank(message = "결제 승인할 주문ID를 입력하세요.")
        String orderId
) {
        public PaymentConfirmServiceRequest toServiceRequest(Long paymentTemporaryId, Long memberId) {
                return new PaymentConfirmServiceRequest(
                        paymentTemporaryId,
                        memberId,
                        reservationId,
                        paymentKey,
                        amount,
                        orderId
                );
        }
}
