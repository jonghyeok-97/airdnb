package airdnb.be.domain.stay.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.service.request.StayAddServiceRequest;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class StayServiceTest {

    @Autowired
    private StayService stayService;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        stayRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("숙소를 등록한다")
    @Test
    void addStay() {
        // given
        Member member = new Member("이름1", "email@naver.com", "010-1234-1234", "password");
        memberRepository.save(member);
        StayAddServiceRequest serviceRequest = new StayAddServiceRequest(
                1L,
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

        // when
        Long stayId = stayService.addStay(serviceRequest);

        // then
        assertThat(stayId).isEqualTo(1L);
    }

    @DisplayName("숙소를 등록하는 회원의 Id가 없으면 예외가 발생한다")
    @Test
    void addStayWithMemberId() {
        // given
        Member member = new Member("이름1", "email@naver.com", "010-1234-1234", "password");
        memberRepository.save(member);
        StayAddServiceRequest serviceRequest = new StayAddServiceRequest(
                2L,
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

        ErrorCode notExistMember = ErrorCode.NOT_EXIST_MEMBER;

        // when then
        assertThatThrownBy(() -> stayService.addStay(serviceRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(notExistMember);
    }
}