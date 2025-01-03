package airdnb.be.web.payment;

import airdnb.be.annotation.argumentResolver.Login;
import airdnb.be.domain.payment.service.PaymentFacade;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.web.ApiResponse;
import airdnb.be.web.payment.request.PaymentConfirmRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/payment/reservation/{reservationId}/request")
    public ApiResponse<Long> addPaymentTemporaryData(@Login Long memberId,
                                                     @PathVariable Long reservationId,
                                                     @RequestParam String paymentKey,
                                                     @RequestParam String amount,
                                                     @RequestParam String orderId) {
        return ApiResponse.ok(
                paymentFacade.addPaymentTemporaryData(memberId, reservationId, paymentKey, amount, orderId)
        );
    }

    @PostMapping("/payment/{paymentTemporaryId}/confirm")
    public ApiResponse<PaymentReservationResponse> confirmPaymentByReservation(@Login Long memberId,
                                                                               @PathVariable Long paymentTemporaryId,
                                                                               @RequestBody PaymentConfirmRequest request) {
        PaymentReservationResponse response = paymentFacade.confirmPaymentByReservation(
                request.toServiceRequest(paymentTemporaryId, memberId));

        return ApiResponse.ok(response);
    }
}
