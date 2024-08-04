package airdnb.be.web.member.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("이메일 최상위 도메인 형식은 2자리 이상 알파벳으로만 구성된다")
    @ParameterizedTest
    @ValueSource(strings = {"gromit@naver.co.1r", "gromit@naver.co.k", "gromit@naver.co.", "gromit@naver.co.d "})
    void validateEmailDomainPart(String email) {
        EmailRequest emailDto = new EmailRequest(email);
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailDto);

        assertThat(violations).hasSize(1);
    }

    @DisplayName("이메일 형식에 @는 1개만 들어간다")
    @ParameterizedTest
    @ValueSource(strings = {"gr@mit@naver.com", "gromit@@naver.com"})
    void validateEmailFormat(String email) {
        EmailRequest emailDto = new EmailRequest(email);
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailDto);

        assertThat(violations).hasSize(1);
    }

    @DisplayName("이메일 형식에 \"\", \" \" 는 들어갈 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateEmpty(String email) {
        EmailRequest emailDto = new EmailRequest(email);
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailDto);

        assertThat(violations).hasSize(1);
    }

    @DisplayName("이메일 형식에 null은 들어갈 수 없다")
    @Test
    void validateNull() {
        EmailRequest emailDto = new EmailRequest(null);
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(emailDto);

        assertThat(violations).hasSize(1);
    }
}