package airdnb.be.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.ReservationRepository;
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

class ReservationServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        stayRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        reservationDateRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입하지 않은 사람은 숙소를 예약할 수 없다.")
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
        assertThatThrownBy(() -> reservationService.reserveV1(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_MEMBER);
    }

    @DisplayName("등록되지 않은 숙소에 예약할 수 없다.")
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
        assertThatThrownBy(() -> reservationService.reserveV1(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_STAY);
    }

    @DisplayName("숙소를 예약하면 1건의 예약과 예약 날짜가 기간 만큼 생성된다")
    @Test
    void reserveStay() {
        // given
        Member member = saveMember();
        Stay stay = saveStay(member.getMemberId());

        ReservationAddServiceRequest request = new ReservationAddServiceRequest(
                stay.getStayId(),
                member.getMemberId(),
                LocalDate.of(2024, 11, 9),
                LocalDate.of(2024, 11, 11),
                3);

        // when
        ReservationResponse response = reservationService.reserveV1(request);

        // then
        LocalDateTime checkIn = LocalDateTime.of(request.checkInDate(), stay.getCheckInTime());
        LocalDateTime checkOut = LocalDateTime.of(request.checkOutDate(), stay.getCheckOutTime());
        BigDecimal totalFee = stay.getFeePerNight()
                .multiply(BigDecimal.valueOf(2))
                .setScale(2);

        assertThat(reservationDateRepository.findAll()).hasSize(2);
        assertThat(reservationRepository.findAll()).hasSize(1);
        assertThat(response.reservationId()).isNotNull();
        assertThat(response)
                .extracting("stayId", "guestId", "checkIn", "checkOut", "totalFee", "guestCount")
                .containsExactly(stay.getStayId(), member.getMemberId(), checkIn, checkOut, totalFee, 3);
    }

    @DisplayName("같은 숙소에 예약 날짜가 겹치면 예약할 수 없다.")
    @Test
    void failWithDuplicatedDates() {
        // given
        Member member = saveMember();
        Stay stay = saveStay(member.getMemberId());

        List<ReservationDate> dates = ReservationDate.of(
                stay.getStayId(),
                LocalDate.of(2024, 11, 10),
                LocalDate.of(2024, 11, 11));
        reservationDateRepository.saveAll(dates);

        ReservationAddServiceRequest request = new ReservationAddServiceRequest(
                stay.getStayId(),
                member.getMemberId(),
                LocalDate.of(2024, 11, 9),
                LocalDate.of(2024, 11, 11),
                3);
        // when then
        assertThatThrownBy(() -> reservationService.reserveV1(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_EXISTS_RESERVATION);
    }

    @DisplayName("예약 날짜가 겹친 채로 같은 숙소를 동시에 예약할 수 없다.")
    @Test
    void reserveConcurrency() throws InterruptedException {
        // given
        Member member = saveMember();
        Stay stay = saveStay(member.getMemberId());

        ReservationAddServiceRequest request1 = new ReservationAddServiceRequest(
                stay.getStayId(),
                member.getMemberId(),
                LocalDate.of(2024, 11, 9),
                LocalDate.of(2024, 11, 14),
                3);

        ReservationAddServiceRequest request2 = new ReservationAddServiceRequest(
                stay.getStayId(),
                member.getMemberId(),
                LocalDate.of(2024, 11, 11),
                LocalDate.of(2024, 11, 13),
                3);

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
                        reservationService.reserveV1(request1);
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
                        reservationService.reserveV1(request2);
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