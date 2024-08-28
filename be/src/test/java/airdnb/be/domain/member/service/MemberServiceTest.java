package airdnb.be.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.member.service.request.MemberLoginServiceRequest;
import airdnb.be.domain.member.service.request.MemberSaveServiceRequest;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이메일로 회원이 존재하는지 확인한다.")
    @Test
    void existsMemberByEmail() {
        // given
        Member member1 = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "2@naver.com", "010-2222-2222", "password");
        Member member3 = new Member("이름3", "3@naver.com", "010-3333-3333", "password");
        memberRepository.saveAll(List.of(member1, member2, member3));

        // when
        boolean result = memberService.existsMemberByEmail("1@naver.com");

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("회원을 등록할 때 이메일이 중복되면 예외가 발생한다")
    @Test
    void addMemberIfDuplicatedEmail() {
        // given
        Member member1 = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "2@naver.com", "010-2222-2222", "password");
        Member member3 = new Member("이름3", "3@naver.com", "010-3333-3333", "password");
        memberRepository.saveAll(List.of(member1, member2, member3));

        MemberSaveServiceRequest serviceRequest = new MemberSaveServiceRequest("이름4", "1@naver.com", "010-5678-56780",
                "password");

        // when then
        assertThatThrownBy(() -> memberService.addMember(serviceRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_EXISTS_MEMBER);
    }

    @DisplayName("회원을 등록한다")
    @Test
    void addMember() {
        // given
        Member member1 = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "2@naver.com", "010-2222-2222", "password");
        Member member3 = new Member("이름3", "3@naver.com", "010-3333-3333", "password");
        memberRepository.saveAll(List.of(member1, member2, member3));

        MemberSaveServiceRequest serviceRequest = new MemberSaveServiceRequest("이름4", "4@naver.com", "010-5678-5678",
                "password");

        // when
        Long savedMemberId = memberService.addMember(serviceRequest);
        Member foundMember = memberRepository.findById(savedMemberId).orElseThrow();

        // then
        assertThat(foundMember.getId()).isPositive();
        assertThat(foundMember)
                .extracting("name", "email", "phoneNumber")
                .containsExactly(
                        "이름4", "4@naver.com", "010-5678-5678"
                );
    }

    @DisplayName("회원은 이메일/패스워드로 로그인을 한다.")
    @Test
    void login() {
        // given
        Member member = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        memberRepository.save(member);

        MemberLoginServiceRequest serviceRequest = new MemberLoginServiceRequest( "1@naver.com", "password");

        // when then
        assertThatCode(() -> memberService.login(serviceRequest))
                .doesNotThrowAnyException();
    }

    @DisplayName("회원의 이메일이 틀리면 로그인 할 수 없다고 예외가 발생한다.")
    @Test
    void loginWithWrongEmail() {
        // given
        Member member = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        memberRepository.save(member);

        MemberLoginServiceRequest serviceRequest = new MemberLoginServiceRequest( "2@naver.com", "password");

        // when then
        assertThatThrownBy(() -> memberService.login(serviceRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CANNOT_LOGIN);
    }

    @DisplayName("회원의 패스워드가 틀리면 로그인 할 수 없다고 예외가 발생한다.")
    @Test
    void loginWithWrongPassword() {
        // given
        Member member = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        memberRepository.save(member);

        MemberLoginServiceRequest serviceRequest = new MemberLoginServiceRequest( "1@naver.com", "password123");

        // when then
        assertThatThrownBy(() -> memberService.login(serviceRequest))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CANNOT_LOGIN);
    }
}