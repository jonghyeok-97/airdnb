package airdnb.be.domain.reservation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.domain.reservation.embedded.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReservationTest {

    @DisplayName("예약을 생성하면 초기 상태는 '예약됨' 이다.")
    @Test
    void initStatusReservedWhenReservationDateCreate() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 9, 1, 15, 0),
                LocalDateTime.of(2024, 9, 5, 11, 0),
                3,
                new BigDecimal(30000)
        );

        // when
        ReservationStatus status = reservation.getStatus();

        // then
        assertThat(status).isEqualTo(ReservationStatus.RESERVED);
    }

    @DisplayName("특정 회원과 특정 숙소에 대한 예약인지 확인할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"1, 1, true", "1, 2, false"})
    void isCreatedByMemberAndStay(Long memberId, Long stayId, boolean expected) {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );

        // when
        boolean result = reservation.isCreatedBy(memberId, stayId);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("예약이 주어진 시간보다 지났으면 끝난다")
    @Test
    void isOver() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );

        // when
        boolean result = reservation.isEnd(LocalDateTime.of(2024, 5, 10, 12, 0));

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("예약이 주어진 시간보다 이전이면 끝나지 않는다.")
    @Test
    void isNotOver() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );

        // when
        boolean result = reservation.isEnd(LocalDateTime.of(2024, 5, 8, 12, 0));

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("예약의 총 금액을 비교한다")
    @ParameterizedTest
    @CsvSource(value = {"30000,true", "50000,false"})
    void hasTotalFee(String amount, boolean hasTotalFee) {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );

        // when
        boolean result = reservation.hasTotalFee(amount);

        // then
        assertThat(result).isEqualTo(hasTotalFee);
    }

    @DisplayName("예약 한 사람이 동일 인물인지 확인한다")
    @ParameterizedTest
    @CsvSource(value = {"1, true", "2, false"})
    void isCreateBySamePerson(Long requestedMemberId, boolean result) {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );

        // when
        boolean actual = reservation.isCreatedBy(requestedMemberId);

        // then
        assertThat(actual).isEqualTo(result);
    }

    @DisplayName("예약의 상태를 변경한다")
    @Test
    void updateStatus() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 5, 2, 15, 0),
                LocalDateTime.of(2024, 5, 10, 11, 1),
                3,
                new BigDecimal(30000)
        );
        ReservationStatus status = ReservationStatus.RESERVED;

        // when
        reservation.updateStatus(status);

        // then
        assertThat(reservation.getStatus()).isEqualTo(status);
    }
}