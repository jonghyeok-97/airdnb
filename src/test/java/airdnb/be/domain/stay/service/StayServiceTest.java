package airdnb.be.domain.stay.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.entity.ReservationDate;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.service.request.StayAddServiceRequest;
import airdnb.be.domain.stay.service.response.StayReservedDatesResponse;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StayServiceTest extends IntegrationTestSupport {

    @Autowired
    private StayService stayService;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    /**
     * AllInBatch - 테이블 자체를 날리는 것 -> 외래키 제약 조건을 고려야 함.
     * deleteAll - 데이터를 지울 때 모든 데이터를 조회한 후, 데이터를 1개씩 지움. sql이 깔끔하지 않음(생각) + 성능문제
     */
    @AfterEach
    void tearDown() {
        stayRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        reservationDateRepository.deleteAllInBatch();
    }

    @DisplayName("숙소를 등록한다")
    @Test
    void addStay() {
        // given
        Member member = createMember("1@naver.com");
        memberRepository.save(member);

        StayAddServiceRequest serviceRequest = createServiceRequest(member.getMemberId());

        // when
        Long savedStayId = stayService.addStay(serviceRequest);

        // then
        Stay foundStay = stayRepository.findById(savedStayId).orElseThrow();

        assertThat(savedStayId).isEqualTo(foundStay.getStayId());
    }

    @DisplayName("가입하지 않은 회원이 숙소를 등록하면 예외가 발생한다")
    @Test
    void addStayWithMemberId() {
        // given
        Long notExistMemberId = 10000L;
        StayAddServiceRequest serviceRequest = createServiceRequest(notExistMemberId);

        // when then
        assertThatThrownBy(() -> stayService.addStay(serviceRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_MEMBER);
    }

    @DisplayName("저장된 숙소Id로 숙소를 찾는다.")
    @Test
    void getStay() {
        // given
        Member member = createMember("1@naver.com");
        memberRepository.save(member);

        Stay saved = stayRepository.save(createStay(member.getMemberId()));

        // when
        StayResponse stayResponse = stayService.getStay(saved.getStayId());

        // then
        assertThat(stayResponse)
                .extracting("hostId", "title", "description", "checkInTime", "checkOutTime", "feePerNight"
                        , "guestCount", "longitude", "latitude", "imageUrls")
                .containsExactly(
                        member.getMemberId(),
                        "제목",
                        "설명",
                        LocalTime.of(15, 0),
                        LocalTime.of(11, 0),
                        new BigDecimal("30000.00"),
                        3,
                        104.2,
                        58.3,
                        List.of("url1", "url2", "url3", "url4", "url5")
                );
    }

    @DisplayName("등록되지 않은 숙소Id로 숙소를 찾으면 예외가 발생한다.")
    @Test
    void getStayWithoutNotExistStay() {
        // given
        Member member = createMember("1@naver.com");
        memberRepository.save(member);

        Stay stay = createStay(member.getMemberId());
        stayRepository.save(stay);

        Long notExistStayId = 1000L;

        // when then
        assertThatThrownBy(() -> stayService.getStay(notExistStayId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_STAY);
    }

    @DisplayName("저장된 숙소의 Id로 숙소를 삭제한다.")
    @Test
    void deleteStay() {
        // given
        Member member = createMember("1@naver.com");
        memberRepository.save(member);

        Stay stay = createStay(member.getMemberId());
        Stay saved = stayRepository.save(stay);

        // when
        stayService.deleteStay(saved.getStayId());

        // then
        assertThat(stayRepository.findAll()).hasSize(0);
    }

    @DisplayName("숙소 이미지를 수정 한다")
    @Test
    void changeStayImage() {
        // given
        Member member = createMember("1@naver.com");
        memberRepository.save(member);

        Stay stay = createStay(member.getMemberId());
        Stay saved = stayRepository.save(stay);

        List<String> target = List.of("1", "2", "3", "4", "5", "6");

        // when
        StayResponse stayResponse = stayService.changeStayImage(saved.getStayId(), target);

        // then
        assertThat(stayResponse.imageUrls())
                .containsExactlyInAnyOrderElementsOf(target);
    }

    @DisplayName("숙소 이미지 수정할 때, 숙소ID가 없으면 예외가 발생한다.")
    @Test
    void changeStayImageWithException() {
        // given
        Long notExistStayId = 1000L;

        // when then
        assertThatThrownBy(() -> stayService.changeStayImage(notExistStayId, List.of()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_STAY);
    }

    @DisplayName("숙소ID로 숙소의 예약된 날짜를 조회한다.")
    @Test
    void getReservedDates() {
        // given
        Member member1 = createMember("1@naver.com");
        Member member2 = createMember("2@naver.com");
        memberRepository.saveAll(List.of(member1, member2));

        Stay stay = createStay(member1.getMemberId());
        Stay savedStay = stayRepository.save(stay);

        ReservationDate date1 = new ReservationDate(
                savedStay.getStayId(),
                LocalDate.of(2024, 5, 30)
        );
        ReservationDate date2 = new ReservationDate(
                savedStay.getStayId(),
                LocalDate.of(2024, 5, 31)
        );
        ReservationDate date3 = new ReservationDate(
                savedStay.getStayId(),
                LocalDate.of(2024, 6, 2)
        );
        ReservationDate date4 = new ReservationDate(
                savedStay.getStayId(),
                LocalDate.of(2024, 6, 3)
        );
        reservationDateRepository.saveAll(List.of(date1, date2, date3, date4));

        // when
        StayReservedDatesResponse result = stayService.getReservedDates(savedStay.getStayId());

        // then
        assertThat(result.reservedDates())
                .containsExactly(
                        LocalDate.of(2024, 5, 30),
                        LocalDate.of(2024, 5, 31),
                        LocalDate.of(2024, 6, 2),
                        LocalDate.of(2024, 6, 3)
                );
    }

    private Member createMember(String email) {
        return new Member("이름1", email, "010-1111-1111", "password");
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
                List.of("url1", "url2", "url3", "url4", "url5")
        );
    }

    private StayAddServiceRequest createServiceRequest(Long memberId) {
        return new StayAddServiceRequest(
                memberId,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(30000),
                3,
                104.2,
                58.3,
                List.of("url1", "url2", "url3", "url4", "url5")
        );
    }
}