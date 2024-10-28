package airdnb.be.domain.payment.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TossCancel {

    private String transactionKey;

    private String cancelReason;

    private int taxExemptionAmount;

    private String cancelStatus;

    @Builder
    private TossCancel(String transactionKey, String cancelReason, int taxExemptionAmount, String cancelStatus) {
        this.transactionKey = transactionKey;
        this.cancelReason = cancelReason;
        this.taxExemptionAmount = taxExemptionAmount;
        this.cancelStatus = cancelStatus;
    }
}
