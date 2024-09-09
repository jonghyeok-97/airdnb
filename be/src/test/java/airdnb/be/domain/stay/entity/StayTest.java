package airdnb.be.domain.stay.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.reservation.Reservation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StayTest {

    @DisplayName("숙소 날짜를 지정해서 숙소 예약을 한다.")
    @Test
    void createReservationByStay() {
        // given
        LocalTime checkInTime = LocalTime.of(15, 0);
        LocalTime checkOutTime = LocalTime.of(11, 0);

        Stay stay = createStay(checkInTime, checkOutTime, 3, new BigDecimal(30000));

        // when
        LocalDate checkInDate = LocalDate.of(2024, 9, 9);
        LocalDate checkOutDate = LocalDate.of(2024, 9, 12);

        Reservation reservation = stay.createReservation(1L,
                checkInDate,
                checkOutDate,
                2);

        // then
        assertThat(reservation.getCheckIn()).isEqualTo(LocalDateTime.of(checkInDate, checkInTime));
        assertThat(reservation.getCheckOut()).isEqualTo(LocalDateTime.of(checkOutDate, checkOutTime));
    }

    @DisplayName("예약 인원이 숙소 인원보다 많으면 예외가 발생한다.")
    @Test
    void validateCapableGuest() {
        // given
        int guestCount = 3;
        Stay stay = createStay(
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                guestCount,
                new BigDecimal(30000));

        // when then
        assertThatThrownBy(() -> stay.createReservation(1L,
                LocalDate.of(2024, 9, 9),
                LocalDate.of(2024, 9, 12),
                5))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CANNOT_CAPABLE_GUEST);
    }

    @DisplayName("숙소는 숙박 요금을 계산한다.")
    @Test
    void calculateTotalFee() {
        // given
        BigDecimal feePerNight = new BigDecimal(30000);
        Stay stay = createStay(
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                3,
                feePerNight);

        // when
        Reservation reservation = stay.createReservation(1L,
                LocalDate.of(2024, 9, 9),
                LocalDate.of(2024, 9, 12),
                2);

        // then
        assertThat(reservation.getTotalFee()).isEqualTo(new BigDecimal(30000 * 3));
    }

    private Stay createStay(LocalTime checkInTime, LocalTime checkOutTime, int guestCount, BigDecimal feePerNight) {
        return new Stay(
                1L,
                "제목",
                "설명",
                checkInTime,
                checkOutTime,
                feePerNight,
                guestCount,
                104.2,
                58.3,
                List.of("url1", "url2", "url3", "url4", "url5")
        );
    }
}