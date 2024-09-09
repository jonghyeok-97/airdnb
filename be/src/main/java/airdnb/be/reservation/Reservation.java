package airdnb.be.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Reservation {

    private Long reservationId;
    private Long stayId;
    private Long guestId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int guestCount;
    private BigDecimal totalFee;

    public Reservation(Long stayId, Long guestId, LocalDateTime checkIn, LocalDateTime checkOut,
                       int guestCount, BigDecimal totalFee) {
        this.stayId = stayId;
        this.guestId = guestId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestCount = guestCount;
        this.totalFee = totalFee;
    }
}
