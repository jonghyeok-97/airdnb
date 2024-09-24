package airdnb.be.domain.member.entitiy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberTest {

    @DisplayName("회원은 비밀번호가 일치하는지 확인할 수 있다.")
    @ParameterizedTest
    @CsvSource({"password, true", "password1, false"})
    void hasPassword(String password, boolean expected) {
        // given
        Member member = new Member("이름1", "1@naver.com", "010-1111-1111", "password");

        // when
        boolean result = member.hasPassword(password);

        // then
        assertThat(result).isEqualTo(expected);
    }
}