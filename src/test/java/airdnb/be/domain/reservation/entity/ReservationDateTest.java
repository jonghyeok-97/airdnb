package airdnb.be.domain.reservation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationDateTest {

    @DisplayName("체크인, 체크아웃으로 예약될 날짜를 생성한다.")
    @Test
    void createReservationDate() {
        // given when
        List<ReservationDate> dates = ReservationDate.of(
                2L,
                LocalDate.of(2024, 9, 1),
                LocalDate.of(2024, 9, 5));

        // then
        assertThat(dates)
                .extracting("reservationDate")
                .containsAnyElementsOf(List.of(
                        LocalDate.of(2024, 9, 1),
                        LocalDate.of(2024, 9, 2),
                        LocalDate.of(2024, 9, 3),
                        LocalDate.of(2024, 9, 4)
                ));
    }

    @DisplayName("숙소ID와 예약 날짜가 같으면 동등하다.")
    @Test
    void equalsAndHashCodeByStayIdAndDate() {
        // given
        List<ReservationDate> dates1 = ReservationDate.of(
                2L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 10));

        List<ReservationDate> dates2 = ReservationDate.of(
                2L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 10));

        // when then
        assertThat(dates1).isEqualTo(dates2);
    }

    @DisplayName("예약 날짜가 같지 않으면 동등하지 않다.")
    @Test
    void equalsAndHashCodeByStayIdAndDate2() {
        // given
        List<ReservationDate> dates1 = ReservationDate.of(
                2L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 7));

        List<ReservationDate> dates2 = ReservationDate.of(
                2L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 8));

        // when then
        assertThat(dates1).isNotEqualTo(dates2);
    }

    @DisplayName("숙소ID가 같지 않으면 동등하지 않다.")
    @Test
    void equalsAndHashCodeByStayIdAndDate3() {
        // given
        List<ReservationDate> dates1 = ReservationDate.of(
                1L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 7));

        List<ReservationDate> dates2 = ReservationDate.of(
                2L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 7));

        // when then
        assertThat(dates1).isNotEqualTo(dates2);
    }
}