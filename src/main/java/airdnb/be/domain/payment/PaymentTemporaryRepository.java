package airdnb.be.domain.payment;

import airdnb.be.domain.payment.entity.PaymentTemporary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTemporaryRepository extends JpaRepository<PaymentTemporary, Long> {
}
