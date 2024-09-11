package airdnb.be.domain.reservation.service.response;

import airdnb.be.domain.reservation.entity.Reservation;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(

        Long reservationId,
        Long stayId,
        Long guestId,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        BigDecimal totalFee

) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getStayId(),
                reservation.getGuestId(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getTotalFee()
        );
    }
}
