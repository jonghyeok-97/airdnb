package airdnb.be.web.payment.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentConfirmRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("결제 승인 요청의 모든 필드에 NULL 이 들어 갈 수 없다.")
    @Test
    void checkPaymentConfirmRequestIsNotNull() {
        // given
        PaymentConfirmRequest request = new PaymentConfirmRequest(
                null,
                null,
                null,
                null
        );

        // when then
        assertThat(validator.validate(request)).hasSize(4);
    }

    @DisplayName("결제 승인 요청의 String 타입의 필드에는 빈값과 공백이 들어 갈 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void checkStringTypeOfPaymentConfirmRequest(String value) {
        // given
        PaymentConfirmRequest request = new PaymentConfirmRequest(
                1L,
                value,
                value,
                value
        );

        // when then
        assertThat(validator.validate(request)).hasSize(3);
    }
}