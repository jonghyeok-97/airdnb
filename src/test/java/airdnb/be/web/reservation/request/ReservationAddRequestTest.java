package airdnb.be.web.reservation.request;

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
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ReservationAddRequestTest extends ControllerTestSupport {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("예약을 등록할 때 숙소Id,회원Id,체크인/아웃 날짜는 null이 될 수 없다.")
    @Test
    void reserveWithNull() throws Exception {
        // given
        ReservationAddRequest request = new ReservationAddRequest(
                null,
                null,
                null,
                1
        );

        // when
        Set<ConstraintViolation<ReservationAddRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.size()).isEqualTo(3);
        mockMvc.perform(
                        post("/reservation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("예약을 등록할 때 게스트 수는 양수이다")
    @Test
    void reserveWithGuestCountIsPositive() throws Exception {
        // given
        ReservationAddRequest request = new ReservationAddRequest(
                1L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 10),
                0
        );
        // when then
        mockMvc.perform(
                        post("/reservation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("0400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("숙박 인원은 최소 1명입니다"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}