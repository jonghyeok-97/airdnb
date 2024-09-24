package airdnb.be.domain.reservation.entity;

import airdnb.be.domain.base.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(exclude = {"reservationDateId", "status"}, callSuper = false)
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"stayId", "reservationDate"},
        name = "stay_id_reservation_date"
))
@Entity
public class ReservationDate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationDateId;

    @Column(nullable = false)
    private Long stayId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDate reservationDate;

    public ReservationDate(Long stayId, LocalDate reservationDate) {
        this.stayId = stayId;
        this.reservationDate = reservationDate;
        this.status = ReservationStatus.RESERVED;
    }
}
