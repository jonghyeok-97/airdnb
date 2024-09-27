package airdnb.be.domain.reservation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.domain.reservation.embedded.ReservationStatus;
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

    @DisplayName("숙소ID와 예약 날짜가 같으면 동등하다.")
    @Test
    void equalsAndHashCodeByStayIdAndDate() {
        // given
        ReservationDate reservationDate1 = new ReservationDate(1L, LocalDate.of(2024, 4, 5));
        ReservationDate reservationDate2 = new ReservationDate(1L, LocalDate.of(2024, 4, 5));

        // when then
        assertThat(reservationDate1).isEqualTo(reservationDate2);
    }

    @DisplayName("예약 날짜가 같지 않으면 동등하지 않다.")
    @Test
    void equalsAndHashCodeByStayIdAndDate2() {
        // given
        ReservationDate reservationDate1 = new ReservationDate(1L, LocalDate.of(2024, 4, 5));
        ReservationDate reservationDate2 = new ReservationDate(1L, LocalDate.of(2024, 4, 6));

        // when then
        assertThat(reservationDate1).isNotEqualTo(reservationDate2);
    }

    @DisplayName("숙소ID가 같지 않으면 동등하지 않다.")
    @Test
    void equalsAndHashCodeByStayIdAndDate3() {
        // given
        ReservationDate reservationDate1 = new ReservationDate(1L, LocalDate.of(2024, 4, 5));
        ReservationDate reservationDate2 = new ReservationDate(2L, LocalDate.of(2024, 4, 5));

        // when then
        assertThat(reservationDate1).isNotEqualTo(reservationDate2);
    }
}