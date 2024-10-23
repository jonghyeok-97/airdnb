package airdnb.be.domain.payment;

import airdnb.be.domain.payment.entity.TossPaymentConfirm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentConfirmRepository extends JpaRepository<TossPaymentConfirm, Long> {
}