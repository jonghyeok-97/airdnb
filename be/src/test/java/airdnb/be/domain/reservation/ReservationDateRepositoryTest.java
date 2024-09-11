package airdnb.be.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.reservation.entity.ReservationDate;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationDateRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    @AfterEach
    void tearDown() {
        reservationDateRepository.deleteAllInBatch();
    }

    @DisplayName("숙소Id로 예약된 날짜를 찾는다.")
    @Test
    void findReservationDatesByStayId() {
        // given
        ReservationDate reservationDate1 = new ReservationDate(1L, LocalDate.of(2024, 8, 13));
        ReservationDate reservationDate2 = new ReservationDate(1L, LocalDate.of(2024, 8, 14));
        ReservationDate reservationDate3 = new ReservationDate(2L, LocalDate.of(2024, 8, 13));
        ReservationDate reservationDate4 = new ReservationDate(3L, LocalDate.of(2024, 8, 15));

        reservationDateRepository.saveAll(
                List.of(reservationDate1, reservationDate2, reservationDate3, reservationDate4));

        // when
        List<ReservationDate> results = reservationDateRepository.findReservationDatesByStayId(1L);

        // then
        assertThat(results).hasSize(2)
                .extracting("reservationDate")
                .containsExactlyInAnyOrderElementsOf(List.of(
                        LocalDate.of(2024, 8, 13),
                        LocalDate.of(2024, 8, 14)));
    }
}