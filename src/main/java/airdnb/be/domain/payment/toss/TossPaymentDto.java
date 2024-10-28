package airdnb.be.domain.payment.toss;

import airdnb.be.domain.payment.entity.TossCancel;
import airdnb.be.domain.payment.entity.TossPaymentStatus;
import java.util.List;

public record TossPaymentDto(

        String paymentKey,

        String orderId,

        String orderName,

        String mId,

        String lastTransactionKey,

        Integer taxExemptionAmount,

        TossPaymentStatus status,

        String requestedAt,

        List<TossCancel> cancels
) {
}
