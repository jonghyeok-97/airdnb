package airdnb.be.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.reservation.entity.ReservationDate;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
        List<ReservationDate> dates1 = ReservationDate.of(
                1L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 7));
        List<ReservationDate> dates2 = ReservationDate.of(
                1L,
                LocalDate.of(2024, 7, 29),
                LocalDate.of(2024, 7, 30));
        List<ReservationDate> dates3 = ReservationDate.of(
                2L,
                LocalDate.of(2024, 4, 8),
                LocalDate.of(2024, 4, 9));
        reservationDateRepository.saveAll(dates1);
        reservationDateRepository.saveAll(dates2);
        reservationDateRepository.saveAll(dates3);

        // when
        List<ReservationDate> results = reservationDateRepository.findReservationDatesByStayId(1L);

        // then
        assertThat(results).hasSize(3)
                .extracting("reservationDate")
                .containsExactly(
                        LocalDate.of(2024, 4, 5),
                        LocalDate.of(2024, 4, 6),
                        LocalDate.of(2024, 7, 29));
    }

    @DisplayName("예약날짜만큼 예약날짜ID가 생성됐는지 확인한다.")
    @Test
    void find() {
        // given
        List<ReservationDate> dates = ReservationDate.of(3L,
                LocalDate.of(2024, 5, 18),
                LocalDate.of(2024, 5, 22));
        reservationDateRepository.saveAll(dates);

        // when
        List<ReservationDate> all = reservationDateRepository.findAll();

        List<Long> allIds = all.stream()
                .map(ReservationDate::getReservationDateId)
                .collect(Collectors.toList());

        // then
        assertThat(allIds).hasSize(4);
    }
}