package airdnb.be.domain.reservation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationDateTest {

    @DisplayName("예약 날짜를 생성하면 초기 상태는 '예약됨'이다.")
    @Test
    void initStatusReservedWhenReservationDateCreate() {
        // given
        ReservationDate reservationDate = new ReservationDate(1L, LocalDate.of(2024, 8, 13));

        // when
        ReservationStatus status = reservationDate.getStatus();

        // then
        assertThat(status).isEqualTo(ReservationStatus.RESERVED);
    }
}