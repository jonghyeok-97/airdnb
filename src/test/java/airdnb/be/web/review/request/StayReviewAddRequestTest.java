package airdnb.be.web.review.request;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.ControllerTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

class StayReviewAddRequestTest extends ControllerTestSupport {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("숙소 리뷰 내용과 별점은 필수이다.")
    @Test
    void validateNullContent() {
        // given
        StayReviewAddRequest request = new StayReviewAddRequest(null, null);

        // when
        Set<ConstraintViolation<StayReviewAddRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(2);
    }

    @DisplayName("숙소 리뷰 내용에는 빈 값, 공백은 들어갈 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void validateContent(String content) throws Exception {
        // given
        StayReviewAddRequest request = new StayReviewAddRequest(content, 3.5d);

        // when
        Set<ConstraintViolation<StayReviewAddRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        mockMvc.perform(
                        post("/stay/{stayId}/review", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("0400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("리뷰 내용을 입력해 주세요"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("숙소 리뷰 별점의 범위가 0 ~ 5점이 아니면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 5.00001})
    void validateStarRating1(double starRating) throws Exception {
        // given
        StayReviewAddRequest request = new StayReviewAddRequest("내용", starRating);

        // when
        Set<ConstraintViolation<StayReviewAddRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        mockMvc.perform(
                        post("/stay/{stayId}/review", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("0400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("별점은 0~5점 사이입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("숙소 리뷰 별점의 범위는 0 ~ 5점 이다.")
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0, 5.0000})
    void validateStarRating2(double starRating) throws Exception {
        // given
        StayReviewAddRequest request = new StayReviewAddRequest("내용", starRating);

        // when
        Set<ConstraintViolation<StayReviewAddRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(0);
        mockMvc.perform(
                        post("/stay/{stayId}/review", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                ).andDo(print())
                .andExpect(status().isOk());
    }
}