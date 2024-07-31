package airdnb.be.member.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("회원가입시 회원이름은 있어야 한다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateMemberName(String name) {
        // given
        MemberDto memberDto = new MemberDto(name, "d@d", "010-1234-5678");

        // when
        Set<ConstraintViolation<MemberDto>> violations = validator.validate(memberDto);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    @DisplayName("회원가입시 이메일 형식을 맞춰야 한다")
    @ParameterizedTest
    @ValueSource(strings = {"dkswhdgur", "@naver.com", "", " "})
    void validateMemberEmail(String email) {
        // given
        MemberDto memberDto = new MemberDto("jong", email, "010-1234-5678");

        // when
        Set<ConstraintViolation<MemberDto>> violations = validator.validate(memberDto);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    @DisplayName("회원가입시 폰번호 형식은 xxx-xxxx-xxxx 이다")
    @ParameterizedTest
    @ValueSource(strings = {"010-113-1234", "", "010-1234-54676"})
    void validateMemberPhoneNumber(String phoneNumber) {
        // given
        MemberDto memberDto = new MemberDto("jong", "d@d", phoneNumber);

        // when
        Set<ConstraintViolation<MemberDto>> violations = validator.validate(memberDto);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }
}