package airdnb.be.domain.stay.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.service.request.StayAddServiceRequest;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
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

    private final Long notExistId = 10000L;

    @AfterEach
    void tearDown() {
        stayRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("숙소를 등록한다")
    @Test
    void addStay() {
        // given
        Member member = new Member("이름1", "email1@naver.com", "010-1111-1111", "password");
        Member savedMember = memberRepository.save(member);

        StayAddServiceRequest serviceRequest = createStayAddServiceRequest(savedMember.getId());

        // when
        Long savedStayId = stayService.addStay(serviceRequest);
        Stay stay = stayRepository.findById(savedStayId).orElseThrow();

        // then
        assertThat(savedStayId).isEqualTo(stay.getStayId());
    }

    @DisplayName("가입하지 않은 회원이 숙소를 등록하면 예외가 발생한다")
    @Test
    void addStayWithMemberId() {
        // given
        Member member1 = new Member("이름1", "email1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "email2@naver.com", "010-1111-1112", "password");
        memberRepository.saveAll(List.of(member1, member2));

        StayAddServiceRequest serviceRequest = createStayAddServiceRequest(notExistId);

        // when then
        assertThatThrownBy(() -> stayService.addStay(serviceRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_MEMBER);
    }

    @DisplayName("저장된 숙소의 Id로 숙소를 찾는다.")
    @Test
    void getStay() {
        // given
        Member member1 = new Member("이름1", "email1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "email2@naver.com", "010-1111-1112", "password");
        memberRepository.saveAll(List.of(member1, member2));
        List<Member> members = memberRepository.saveAll(List.of(member1, member2));
        Member member = members.get(0);

        StayAddServiceRequest serviceRequest = createStayAddServiceRequest(member.getId());
        Long savedStayId = stayService.addStay(serviceRequest);

        // when
        StayResponse stayResponse = stayService.getStay(savedStayId);

        // then
        assertThat(stayResponse)
                .extracting("memberId", "title", "description", "checkInTime", "checkOutTime", "feePerNight"
                        , "guestCount", "longitude", "latitude", "imageUrls")
                .containsExactly(
                        member.getId(),
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

    @DisplayName("저장되지 않은 숙소의 Id로 숙소를 찾으면 예외가 발생한다.")
    @Test
    void getStayWithoutNotExistStay() {
        // given
        Member member1 = new Member("이름1", "email1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "email2@naver.com", "010-1111-1112", "password");
        memberRepository.saveAll(List.of(member1, member2));
        List<Member> members = memberRepository.saveAll(List.of(member1, member2));
        Member member = members.get(0);

        StayAddServiceRequest serviceRequest = createStayAddServiceRequest(member.getId());
        stayService.addStay(serviceRequest);

        // when then
        assertThatThrownBy(() -> stayService.getStay(notExistId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_EXIST_STAY);
    }

    @DisplayName("저장된 숙소의 Id로 숙소를 삭제한다.")
    @Test
    void deleteStay() {
        // given
        Member member1 = new Member("이름1", "email1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "email2@naver.com", "010-1111-1112", "password");
        memberRepository.saveAll(List.of(member1, member2));
        List<Member> members = memberRepository.saveAll(List.of(member1, member2));
        Member member = members.get(0);

        StayAddServiceRequest serviceRequest = createStayAddServiceRequest(member.getId());
        Long savedStayId = stayService.addStay(serviceRequest);

        // when
        stayService.deleteStay(savedStayId);

        // when then
        assertThat(stayRepository.findAll()).hasSize(0);
    }

    private StayAddServiceRequest createStayAddServiceRequest(Long memberId) {
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