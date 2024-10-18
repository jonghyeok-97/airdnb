package airdnb.be.web.review;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import airdnb.be.ControllerTestSupport;
import airdnb.be.domain.review.response.ReviewResponse;
import airdnb.be.web.review.request.StayReviewAddRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ReviewControllerTest extends ControllerTestSupport {

    @DisplayName("숙소 리뷰를 등록한다")
    @Test
    void addStayReview() throws Exception {
        // given
        Long stayId = 1L;
        StayReviewAddRequest request = new StayReviewAddRequest(1L, "내용", 3.5d);

        ReviewResponse response = new ReviewResponse(
                1L,
                3L,
                "내용",
                4d
        );

        given(reviewService.addStayReview(any(), any(), any(), any()))
                        .willReturn(response);

        // when then
        mockMvc.perform(
                post("/stay/{stayId}/review", stayId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .sessionAttr(LOGIN_MEMBER, 1L))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());
    }
}