package airdnb.be.web.reservation.request;

import java.time.LocalDate;

public record ReservationAddRequest(
        Long stayId,
        Long guestId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int guestCount
) {
}
