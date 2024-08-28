package airdnb.be.domain.member.entitiy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("회원은 비밀번호가 일치하면 True 이다.")
    @Test
    void hasPassword() {
        // given
        Member member = new Member("이름1", "1@naver.com", "010-1111-1111", "password");

        // when
        boolean result = member.hasPassword("password");

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("회원은 비밀번호가 일치하지 않으면 false 이다.")
    @Test
    void hasNotPassword() {
        // given
        Member member = new Member("이름1", "1@naver.com", "010-1111-1111", "password");

        // when
        boolean result = member.hasPassword("password123");

        // then
        assertThat(result).isFalse();
    }
}