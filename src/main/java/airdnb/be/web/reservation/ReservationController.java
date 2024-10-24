package airdnb.be.web.reservation;

import airdnb.be.annotation.argumentResolver.Login;
import airdnb.be.domain.reservation.service.ReservationServiceV2;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.web.ApiResponse;
import airdnb.be.web.reservation.request.ReservationAddRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceV2 reservationServiceV2;

    @PostMapping("/reservation")
    public ApiResponse<ReservationResponse> reserve(
            @Login Long memberId,
            @RequestBody @Valid ReservationAddRequest request) {
        ReservationResponse response = reservationServiceV2.pendReservation(request.toServiceRequest(memberId));
        return ApiResponse.ok(response);
    }
}
