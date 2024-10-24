package airdnb.be.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.entity.ReservationDate;
import airdnb.be.domain.reservation.service.request.ReservationAddServiceRequest;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationServiceV2Test extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationServiceV2 reservationServiceV2;

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        stayRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        reservationDateRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입하지 않은 사람은 숙소 예약을 신청할 수 없다.")
    @Test
    void cannotReserveNotExistMember() {
        // given
        Long notExistMemberId = 1000L;

        ReservationAddServiceRequest request = new ReservationAddServiceRequest(
                1L,
                notExistMemberId,
                LocalDate.of(2024, 11, 9),
                LocalDate.of(2024, 11, 23),
                3);

        // when then
        assertThatThrownBy(() -> reservationServiceV2.pendReservation(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_MEMBER);
    }

    @DisplayName("등록되지 않은 숙소에 예약을 신청할 수 없다.")
    @Test
    void cannotReserveNotExistStay() {
        // given
        Member member = saveMember();
        Long notExistStayId = 1000L;

        ReservationAddServiceRequest request = new ReservationAddServiceRequest(
                notExistStayId,
                member.getMemberId(),
                LocalDate.of(2024, 11, 9),
                LocalDate.of(2024, 11, 23),
                3);

        // when then
        assertThatThrownBy(() -> reservationServiceV2.pendReservation(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_STAY);
    }

    @DisplayName("결제 전에 예약 정보를 저장한다.")
    @Test
    void pendReservation() {
        // given
        Member member = saveMember();
        Stay stay = saveStay(member.getMemberId());

        ReservationAddServiceRequest request = new ReservationAddServiceRequest(
                stay.getStayId(),
                member.getMemberId(),
                LocalDate.of(2024, 11, 9),
                LocalDate.of(2024, 11, 12),
                3);

        // when
        ReservationResponse response = reservationServiceV2.pendReservation(request);

        // then
        LocalDateTime checkIn = LocalDateTime.of(request.checkInDate(), stay.getCheckInTime());
        LocalDateTime checkOut = LocalDateTime.of(request.checkOutDate(), stay.getCheckOutTime());
        BigDecimal totalFee = stay.getFeePerNight()
                .multiply(BigDecimal.valueOf(3))
                .setScale(2);

        assertThat(reservationRepository.findAll()).hasSize(1);
        assertThat(response.reservationId()).isNotNull();
        assertThat(response)
                .extracting("stayId", "guestId", "checkIn", "checkOut", "totalFee", "guestCount")
                .containsExactly(stay.getStayId(), member.getMemberId(), checkIn, checkOut, totalFee, 3);
    }

    @DisplayName("숙소를 확정하면 1건의 예약과 예약 날짜가 기간 만큼 생성된다")
    @Test
    void reserveStay() {
        // given
        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 8, 13, 15, 0),
                LocalDateTime.of(2024, 8, 16, 11, 0),
                3,
                new BigDecimal(50000)
        );
        Reservation saved = reservationRepository.save(reservation);

        // when
        reservationServiceV2.confirmReservation(saved.getReservationId(), 1L);

        // then
        assertThat(reservationDateRepository.findAll()).hasSize(3)
                .containsAll(
                        ReservationDate.of(1L, LocalDate.of(2024, 8, 13), LocalDate.of(2024, 8, 16)));
    }

    @DisplayName("같은 숙소에 예약 날짜가 겹치면 예약할 수 없다.")
    @Test
    void failWithDuplicatedDates() {
        // given
        List<ReservationDate> dates = ReservationDate.of(
                1L,
                LocalDate.of(2024, 11, 10),
                LocalDate.of(2024, 11, 11));
        reservationDateRepository.saveAll(dates);

        Reservation reservation = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 11, 9, 15, 0),
                LocalDateTime.of(2024, 11, 11, 11, 0),
                3,
                new BigDecimal(50000)
        );
        Reservation saved = reservationRepository.save(reservation);

        // when then
        assertThatThrownBy(() -> reservationServiceV2.confirmReservation(saved.getReservationId(), 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_EXISTS_RESERVATION);
    }

    @DisplayName("서로 다른 사람이 예약 날짜가 겹친 채로 같은 숙소를 동시에 예약할 수 없다.")
    @Test
    void reserveConcurrency() throws InterruptedException {
        // given
        Reservation reservation1 = new Reservation(
                1L,
                1L,
                LocalDateTime.of(2024, 11, 9, 15, 0),
                LocalDateTime.of(2024, 11, 11, 11, 0),
                3,
                new BigDecimal(50000)
        );
        Reservation reservation2 = new Reservation(
                1L,
                2L,
                LocalDateTime.of(2024, 11, 10, 15, 0),
                LocalDateTime.of(2024, 11, 13, 11, 0),
                3,
                new BigDecimal(50000)
        );
        Reservation saved1 = reservationRepository.save(reservation1);
        Reservation saved2 = reservationRepository.save(reservation2);

        int requestCount = 30;
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch latch = new CountDownLatch(requestCount);

        // when
        for (int i = 0; i < requestCount; i++) {
            if (i % 2 == 0) {
                executorService.execute(() -> {
                    try {
                        reservationServiceV2.confirmReservation(saved1.getReservationId(), 1L);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            } else {
                executorService.execute(() -> {
                    try {
                        reservationServiceV2.confirmReservation(saved2.getReservationId(), 2L);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(1);
    }

    private Stay saveStay(Long memberId) {
        Stay stay = createStay(memberId);
        return stayRepository.save(stay);
    }

    private Member saveMember() {
        return memberRepository.save(
                new Member("이름1", "email1@naver.com", "010-1111-1111", "password"));
    }

    private Stay createStay(Long memberId) {
        return new Stay(
                memberId,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(30000),
                3,
                104.2,
                58.3,
                List.of()
        );
    }

}