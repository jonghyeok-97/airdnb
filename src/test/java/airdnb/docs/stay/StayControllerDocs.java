package airdnb.docs.stay;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static airdnb.docs.common.DateTimeFormat.getDateFormat;
import static airdnb.docs.common.DateTimeFormat.getTimeFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.stay.service.StayService;
import airdnb.be.domain.stay.service.response.StayReservedDatesResponse;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.web.stay.StayController;
import airdnb.be.web.stay.request.StayAddRequest;
import airdnb.docs.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
        StayAddRequest request = new StayAddRequest(
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(30000),
                3,
                104.2,
                58.3,
                List.of("url1", "url2", "url3", "url4", "url5")
        );

        Long stayId = 1L;
        given(stayService.addStay(any()))
                .willReturn(stayId);

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "ACBCDFD0FF93D5BB"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(stayId))
                .andDo(document("stay/stay-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(cookieWithName("JSESSIONID").description("로그인 세션 쿠키")),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("숙소 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("숙소 설명"),
                                fieldWithPath("checkInTime").type(JsonFieldType.STRING)
                                        .attributes(getTimeFormat())
                                        .description("숙소 체크인 시간"),
                                fieldWithPath("checkOutTime").type(JsonFieldType.STRING)
                                        .attributes(getTimeFormat())
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
                List.of("URL1", "URL2", "URL3", "URL4", "URL5")
        );

        given(stayService.getStay(any()))
                .willReturn(stayResponse);

        // when then
        mockMvc.perform(
                        // REST docs 의 path parameter 에서 MockMvcRequestBuilders 를 사용하면 실패한다
                        RestDocumentationRequestBuilders.get("/stay/{stayId}", stayId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("stay/stay-get-one",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                attributes(key("url").value("/stay/{stayId}")),
                                parameterWithName("stayId").description("조회할 숙소 ID")
                        ),
                        responseFields(
                                beneathPath("data"), // data 아래의 필드만 명시, code/status/message 필드는 공통 응답으로 뺌
                                fieldWithPath("stayId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                fieldWithPath("hostId").type(JsonFieldType.NUMBER).description("숙소 주인ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("숙소 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("숙소 설명"),
                                fieldWithPath("checkInTime").type(JsonFieldType.STRING).attributes(getTimeFormat())
                                        .description("숙소 체크인 시간"),
                                fieldWithPath("checkOutTime").type(JsonFieldType.STRING).attributes(getTimeFormat())
                                        .description("숙소 체크아웃 시간"),
                                fieldWithPath("feePerNight").type(JsonFieldType.NUMBER).description("1박당 요금"),
                                fieldWithPath("guestCount").type(JsonFieldType.NUMBER).description("숙박 인원 수"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("숙소 경도"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("숙소 위도"),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("숙소 이미지 url")
                        )
                ));
    }

    @DisplayName("숙소 삭제 API")
    @Test
    void deleteStay() throws Exception {
        //given
        Long stayId = 3L;

        doNothing()
                .when(stayService)
                .deleteStay(stayId);

        // when then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/stay/{stayId}", stayId)
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "ACBCDFD0FF93D5BB"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("stay/stay-delete",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                attributes(key("url").value("/stay/{stayId}")),
                                parameterWithName("stayId").description("삭제할 숙소 Id")
                        ),
                        requestCookies(cookieWithName("JSESSIONID").description("로그인 세션 쿠키"))));
    }

    @DisplayName("숙소 이미지 업데이트 API")
    @Test
    void changeStayImage() throws Exception {
        //given
        Long stayId = 3L;
        List<String> imageUrls = List.of(
                "changeURL1",
                "changeURL2",
                "changeURL3",
                "changeURL4",
                "changeURL5",
                "changeURL6");

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
                imageUrls
        );

        given(stayService.changeStayImage(anyLong(), any()))
                .willReturn(stayResponse);

        // when then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.put("/stay/{stayId}/image", stayId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageUrls))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "ACBCDFD0FF93D5BB"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("stay/stay-image-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                attributes(key("url").value("/stay/{stayId}/image")),
                                parameterWithName("stayId").description("이미지 업데이트할 숙소 Id")
                        ),
                        requestFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("업데이트할 숙소 이미지(최소 5개)")
                        ),
                        responseFields(
                                beneathPath("data"),
                                fieldWithPath("stayId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                fieldWithPath("hostId").type(JsonFieldType.NUMBER).description("숙소 주인ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("숙소 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("숙소 설명"),
                                fieldWithPath("checkInTime").type(JsonFieldType.STRING).attributes(getTimeFormat())
                                        .description("숙소 체크인 시간"),
                                fieldWithPath("checkOutTime").type(JsonFieldType.STRING).attributes(getTimeFormat())
                                        .description("숙소 체크아웃 시간"),
                                fieldWithPath("feePerNight").type(JsonFieldType.NUMBER).description("1박당 요금"),
                                fieldWithPath("guestCount").type(JsonFieldType.NUMBER).description("숙박 인원 수"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("숙소 경도"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("숙소 위도"),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("숙소 이미지 url")
                        )
                ));
    }

    @DisplayName("숙소의 예약된 날짜 조회 API")
    @Test
    void getReservedDates() throws Exception {
        //given
        Long stayId = 3L;
        StayReservedDatesResponse response = new StayReservedDatesResponse(List.of(
                LocalDate.of(2024, 5, 10),
                LocalDate.of(2024, 5, 11),
                LocalDate.of(2024, 5, 12),
                LocalDate.of(2024, 5, 13)));

        String apiUrl = "/stay/{stayId}/reservedDates";

        given(stayService.getReservedDates(anyLong()))
                .willReturn(response);

        // when then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get(apiUrl, stayId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("stay/stay-reservedDates",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                attributes(key("url").value(apiUrl)),
                                parameterWithName("stayId").description("조회할 숙소 Id")
                        ),
                        responseFields(
                                beneathPath("data"),
                                fieldWithPath("reservedDates").type(JsonFieldType.ARRAY).attributes(getDateFormat())
                                        .description("숙소의 예약된 날짜")
                        )
                ));
    }
}
