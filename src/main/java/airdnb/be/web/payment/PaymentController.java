package airdnb.be.web.payment;

import airdnb.be.annotation.argumentResolver.Login;
import airdnb.be.domain.payment.PaymentService;
import airdnb.be.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/reservation/request")
    public ApiResponse<Void> addPaymentTemporaryData(@Login Long memberId,
                                               @RequestParam String paymentKey,
                                               @RequestParam String amount,
                                               @RequestParam String orderId) {
        paymentService.addPaymentTemporaryData(memberId, paymentKey, amount, orderId);
        return ApiResponse.ok();
    }
}
