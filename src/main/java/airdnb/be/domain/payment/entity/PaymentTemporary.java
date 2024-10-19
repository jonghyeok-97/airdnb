package airdnb.be.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTemporary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentTemporaryId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String amount;

    public PaymentTemporary(Long memberId, Long reservationId, String orderId, String paymentKey, String amount) {
        this.memberId = memberId;
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }
}
