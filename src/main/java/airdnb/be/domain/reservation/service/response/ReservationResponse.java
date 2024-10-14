package airdnb.be.domain.reservation.service.response;

import airdnb.be.domain.reservation.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(

        Long reservationId,
        Long stayId,
        Long guestId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkIn,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime checkOut,

        BigDecimal totalFee,

        int guestCount
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getStayId(),
                reservation.getGuestId(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getTotalFee(),
                reservation.getGuestCount()
        );
    }
}
