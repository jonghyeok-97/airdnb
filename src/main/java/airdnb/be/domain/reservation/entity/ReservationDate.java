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
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(exclude = {"reservationDateId"}, callSuper = false)
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

    private LocalDate reservationDate;

    private ReservationDate(Long stayId, LocalDate reservationDate) {
        this.stayId = stayId;
        this.reservationDate = reservationDate;
    }

    /**
     * @param checkInDate 9/1
     * @param checkOutDate 9/5
     * @return 9/1 ~ 9/4 을 반환
     */
    public static List<ReservationDate> of(Long stayId, LocalDate checkInDate, LocalDate checkOutDate) {
        return checkInDate.datesUntil(checkOutDate)
                .map(date -> new ReservationDate(stayId, date))
                .collect(Collectors.toList());
    }
}
