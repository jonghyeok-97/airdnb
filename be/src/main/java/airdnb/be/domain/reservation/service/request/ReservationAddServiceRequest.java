package airdnb.be.domain.reservation.service.request;

import java.time.LocalDate;

public record ReservationAddServiceRequest(

        Long stayId,
        Long guestId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer guestCount

) {
}
