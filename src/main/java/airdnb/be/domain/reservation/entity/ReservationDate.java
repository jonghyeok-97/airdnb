package airdnb.be.domain.reservation.entity;

import airdnb.be.domain.base.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@EqualsAndHashCode(exclude = {"reservationDateId", "reservationId"}, callSuper = false)
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
    private Long reservationId;

    @Column(nullable = false)
    private Long stayId;

    private LocalDate reservationDate;

    public ReservationDate(Long reservationId, Long stayId, LocalDate reservationDate) {
        this.reservationId = reservationId;
        this.stayId = stayId;
        this.reservationDate = reservationDate;
    }
}
