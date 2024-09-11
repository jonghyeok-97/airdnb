package airdnb.be.web.reservation.request;

import airdnb.be.domain.reservation.service.request.ReservationAddServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record ReservationAddRequest(

        @NotNull(message = "숙소는 필수입니다")
        Long stayId,

        @NotNull(message = "게스트는 필수입니다")
        Long guestId,

        @NotNull
        LocalDate checkInDate,

        @NotNull
        LocalDate checkOutDate,

        @Positive(message = "숙박 인원은 최소 1명입니다")
        int guestCount

) {
        public ReservationAddServiceRequest toServiceRequest() {
                return new ReservationAddServiceRequest(
                        stayId,
                        guestId,
                        checkInDate,
                        checkOutDate,
                        guestCount
                );
        }
}
