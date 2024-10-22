package airdnb.be.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * field설명 : https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class TossPaymentConfirm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tossPaymentConfirmId;

    @Column(nullable = false, length = 200)
    private String paymentKey;

    @Column(nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false, length = 100)
    private String orderName;

    @Column(nullable = false, length = 14)
    private String mId;

    @Column(length = 64)
    private String lastTransactionKey;

    @Column(nullable = false)
    private Integer taxExemptionAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TossPaymentStatus status;

    @Column(nullable = false)
    private String requestedAt;

    @Builder
    private TossPaymentConfirm(String paymentKey, String orderId, String orderName, String mId,
                              String lastTransactionKey,
                              Integer taxExemptionAmount, TossPaymentStatus status, String requestedAt) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.mId = mId;
        this.lastTransactionKey = lastTransactionKey;
        this.taxExemptionAmount = taxExemptionAmount;
        this.status = status;
        this.requestedAt = requestedAt;
    }
}
