package airdnb.be.domain.reservation.entity;

import airdnb.be.domain.base.entity.BaseTimeEntity;
import airdnb.be.domain.reservation.embedded.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Reservation extends BaseTimeEntity {

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

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Reservation(Long stayId, Long guestId, LocalDateTime checkIn, LocalDateTime checkOut,
                       int guestCount, BigDecimal totalFee) {
        this.stayId = stayId;
        this.guestId = guestId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestCount = guestCount;
        this.totalFee = totalFee;
        this.status = ReservationStatus.PENDING;
    }

    public boolean isCreatedBy(Long memberId) {
        return this.guestId.equals(memberId);
    }

    public boolean isCreatedBy(Long memberId, Long stayId) {
        return this.guestId.equals(memberId) && this.stayId.equals(stayId);
    }

    public boolean isEnd(LocalDateTime now) {
        return status == ReservationStatus.RESERVED && now.isAfter(checkOut);
    }

    public boolean hasTotalFee(String totalFee) {
        return this.totalFee.compareTo(new BigDecimal(totalFee)) == 0;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}
