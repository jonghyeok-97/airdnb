package airdnb.be.domain.payment.service.response;

import airdnb.be.domain.reservation.service.response.ReservationResponse;

public record PaymentReservationResponse(

        PaymentConfirmResponse paymentConfirmResponse,
        ReservationResponse reservationResponse

){
    public static PaymentReservationResponse of(PaymentConfirmResponse paymentConfirmResponse, ReservationResponse reservationResponse) {
        return new PaymentReservationResponse(
                paymentConfirmResponse, reservationResponse);
    }
}
