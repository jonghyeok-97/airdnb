package airdnb.docs.reservation;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static airdnb.docs.common.DateTimeFormat.getDateFormat;
import static airdnb.docs.common.DateTimeFormat.getDateTimeFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.reservation.service.ReservationService;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.web.reservation.ReservationController;
import airdnb.be.web.reservation.request.ReservationAddRequest;
import airdnb.docs.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ReservationControllerDocs extends RestDocsSupport {

    private final ReservationService reservationService = mock(ReservationService.class);

    @Override
    protected Object initController() {
        return new ReservationController(reservationService);
    }

    @DisplayName("숙소 예약 API")
    @Test
    void reserve() throws Exception {
        // given
        ReservationAddRequest request = new ReservationAddRequest(
                1L,
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 30),
                3
        );

        ReservationResponse response = new ReservationResponse(
                1L,
                1L,
                1L,
                LocalDateTime.of(2024, 4, 5, 15,0),
                LocalDateTime.of(2024, 4, 30, 11,0),
                new BigDecimal(50000)
        );

        BDDMockito.given(reservationService.reserve(any()))
                .willReturn(response);

        // when then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/reservation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "RBDHAOVN13421D")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("/reservation/reservation-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("stayId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                fieldWithPath("checkInDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("체크인 날짜"),
                                fieldWithPath("checkOutDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("체크아웃 날짜"),
                                fieldWithPath("guestCount").type(JsonFieldType.NUMBER).description("숙소 사용할 인원 수")
                        ),
                        requestCookies(
                                cookieWithName("JSESSIONID").description("로그인 세션 쿠키")
                        ),
                        responseFields(
                                beneathPath("data"),
                                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예약 ID"),
                                fieldWithPath("stayId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                fieldWithPath("guestId").type(JsonFieldType.NUMBER).description("예약한 사람 ID"),
                                fieldWithPath("checkIn").type(JsonFieldType.STRING).attributes(getDateTimeFormat()).description("체크인 날짜/시간"),
                                fieldWithPath("checkOut").type(JsonFieldType.STRING).attributes(getDateTimeFormat()).description("체크아웃 날짜/시간"),
                                fieldWithPath("totalFee").type(JsonFieldType.NUMBER).description("예약 총 요금")
                        )));
    }
}
