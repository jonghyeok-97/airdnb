package airdnb.be.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.domain.member.entitiy.Member;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입을 했으면 이메일로 찾았을 때 True 이다.")
    @ParameterizedTest
    @ValueSource(strings = {"1@naver.com", "2@naver.com", "3@naver.com"})
    void existsByEmail(String targetEmail) {
        // given
        Member member1 = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "2@naver.com", "010-2222-2222", "password");
        Member member3 = new Member("이름3", "3@naver.com", "010-3333-3333", "password");
        memberRepository.saveAll(List.of(member1, member2, member3));

        // when
        boolean result = memberRepository.existsByEmail(targetEmail);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("회원가입을 하지 않았다면 이메일로 찾았을 때 false 이다.")
    @ParameterizedTest
    @ValueSource(strings = {"4@naver.com", "11@naver.com", ""})
    void existsByEmailNotRegister(String targetEmail) {
        // given
        Member member1 = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "2@naver.com", "010-2222-2222", "password");
        Member member3 = new Member("이름3", "3@naver.com", "010-3333-3333", "password");
        memberRepository.saveAll(List.of(member1, member2, member3));

        // when
        boolean result = memberRepository.existsByEmail(targetEmail);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("이메일로 회원을 찾는다.")
    @Test
    void findMemberByEmail() {
        // given
        Member member1 = new Member("이름1", "1@naver.com", "010-1111-1111", "password");
        Member member2 = new Member("이름2", "2@naver.com", "010-2222-2222", "password");
        Member member3 = new Member("이름3", "3@naver.com", "010-3333-3333", "password");
        memberRepository.saveAll(List.of(member1, member2, member3));

        // when
        Member member = memberRepository.findMemberByEmail("1@naver.com");

        // then
        assertThat(member)
                .extracting("name", "email", "phoneNumber")
                .containsExactly(
                        "이름1", "1@naver.com", "010-1111-1111"
                );
    }
}