package airdnb.docs.stay;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.stay.service.StayService;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.web.stay.StayController;
import airdnb.be.web.stay.request.StayAddRequest;
import airdnb.docs.RestDocsSupport;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class StayControllerDocs extends RestDocsSupport {

    private final StayService stayService = Mockito.mock(StayService.class);

    @Override
    protected Object initController() {
        return new StayController(stayService);
    }

    @DisplayName("숙소 등록하는 API")
    @Test
    void createStay() throws Exception {
        // given
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(30000),
                3,
                104.2,
                58.3,
                List.of("url1", "url2", "url3", "url4", "url5")
        );;

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNumber())
                .andDo(document("stay-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                                        .description("등록하려는 회원ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("숙소 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("숙소 설명"),
                                fieldWithPath("checkInTime").type(JsonFieldType.STRING)
                                        .description("숙소 체크인 시간"),
                                fieldWithPath("checkOutTime").type(JsonFieldType.STRING)
                                        .description("숙소 체크아웃 시간"),
                                fieldWithPath("feePerNight").type(JsonFieldType.NUMBER)
                                        .description("1박당 요금(최소 10000원)"),
                                fieldWithPath("guestCount").type(JsonFieldType.NUMBER)
                                        .description("게스트 인원 수(최소 1명)"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("숙소 경도(-180도 ~ 180도 사이)"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("숙소 위도(-90도 ~ 90도 사이"),
                                fieldWithPath("images").type(JsonFieldType.ARRAY)
                                        .description("숙소 이미지(최소 5개)")
                                ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER)
                                        .description("응답 데이터")
                        )
                ));
    }

    @DisplayName("숙소 단건 조회 API")
    @Test
    void getStay() throws Exception {
        //given
        Long stayId = 1L;
        StayResponse stayResponse = new StayResponse(
                stayId,
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(50000),
                3,
                108.4,
                45,
                List.of()
        );

        given(stayService.getStay(stayId))
                .willReturn(stayResponse);

        // when then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/stay/{stayId}", stayId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("get-stay-one",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                attributes(key("url").value("/stay/{stayId}")),
                                parameterWithName("stayId").description("조회할 숙소 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.stayId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                fieldWithPath("data.hostId").type(JsonFieldType.NUMBER).description("숙소 주인ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("숙소 제목"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("숙소 설명"),
                                fieldWithPath("data.checkInTime").type(JsonFieldType.STRING).description("숙소 체크인 시간"),
                                fieldWithPath("data.checkOutTime").type(JsonFieldType.STRING).description("숙소 체크아웃 시간"),
                                fieldWithPath("data.feePerNight").type(JsonFieldType.NUMBER).description("1박당 요금"),
                                fieldWithPath("data.guestCount").type(JsonFieldType.NUMBER).description("숙박 인원 수"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("숙소 경도"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("숙소 위도"),
                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("숙소 이미지 url")
                        )
                ));
    }
}
