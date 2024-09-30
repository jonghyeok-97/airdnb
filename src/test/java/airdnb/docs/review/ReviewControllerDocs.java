package airdnb.docs.review;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.review.ReviewService;
import airdnb.be.domain.review.request.StayReviewAddServiceRequest;
import airdnb.be.domain.review.response.ReviewResponse;
import airdnb.be.web.review.ReviewController;
import airdnb.be.web.review.request.StayReviewAddRequest;
import airdnb.docs.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReviewControllerDocs extends RestDocsSupport {

    private final ReviewService reviewService = Mockito.mock(ReviewService.class);

    @Override
    protected Object initController() {
        return new ReviewController(reviewService);
    }

    @DisplayName("숙소 리뷰 추가 API")
    @Test
    void addStayReview() throws Exception {
        //given
        Long stayId = 1L;

        StayReviewAddRequest request = new StayReviewAddRequest(
                "내용",
                3.5d
        );

        ReviewResponse response = new ReviewResponse(
                1L,
                3L,
                "내용",
                3.5d
        );

        String apiUrl = "/stay/{stayId}/review";

        given(reviewService.addStayReview(any(), any(), any(StayReviewAddServiceRequest.class)))
                .willReturn(response);

        // when then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post(apiUrl, stayId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "DAJEBVB31FBD"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("/review/review-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                attributes(key("url").value(apiUrl)),
                                parameterWithName("stayId").description("리뷰 쓸 숙소 Id")
                        ),
                        requestCookies(
                                cookieWithName("JSESSIONID").description("로그인 세션 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("starRating").type(JsonFieldType.NUMBER).description("리뷰 별점")
                        ),
                        responseFields(
                                beneathPath("data"),
                                fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("writerId").type(JsonFieldType.NUMBER).description("리뷰 작성자 ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("starRating").type(JsonFieldType.NUMBER).description("리뷰 별점")
                        )
                ));
    }
}
