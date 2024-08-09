package airdnb.be.web.member.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberSaveRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("회원가입시 회원이름은 있어야 한다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateMemberName(String name) {
        // given
        MemberSaveRequest request = new MemberSaveRequest(name, "gromit123@naver.com", "010-1234-5678");

        // when
        Set<ConstraintViolation<MemberSaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    @DisplayName("회원가입시 이메일 형식을 맞춰야 한다")
    @ParameterizedTest
    @ValueSource(strings = {"dkswhdgur", "@naver.com", "", " "})
    void validateMemberEmail(String email) {
        // given
        MemberSaveRequest request = new MemberSaveRequest("jong", email, "010-1234-5678");

        // when
        Set<ConstraintViolation<MemberSaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    @DisplayName("회원가입시 폰번호 형식은 010-xxxx-xxxx 이다")
    @ParameterizedTest
    @ValueSource(strings = {"010-113-1234", "", "010-1234-54676", "010-1234-547I"})
    void validateMemberPhoneNumber(String phoneNumber) {
        // given
        MemberSaveRequest request = new MemberSaveRequest("jong", "gromit1234@naver.com", phoneNumber);

        // when
        Set<ConstraintViolation<MemberSaveRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }
}