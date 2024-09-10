package airdnb.be.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private Long stayId;

    @Column(nullable = false)
    private Long guestId;

    @Column(nullable = false)
    private LocalDateTime checkIn;

    @Column(nullable = false)
    private LocalDateTime checkOut;

    @Column(nullable = false)
    private int guestCount;

    @Column(nullable = false)
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

    /**
     * @return 9/1 ~ 9/5 일 때, 예약될 날짜로 9/1 ~ 9/4 을 생성한다.
     */
    public List<ReservationDate> createReservationDate() {
        return checkIn.toLocalDate().datesUntil(checkOut.toLocalDate())
                .map(checkInDate -> new ReservationDate(stayId, checkInDate))
                .collect(Collectors.toList());
    }
}