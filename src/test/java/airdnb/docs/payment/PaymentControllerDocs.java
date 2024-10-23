package airdnb.docs.payment;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static airdnb.docs.common.DateTimeFormat.getDateTimeFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.payment.service.PaymentFacade;
import airdnb.be.domain.payment.service.response.PaymentConfirmResponse;
import airdnb.be.domain.payment.service.response.PaymentReservationResponse;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.web.payment.PaymentController;
import airdnb.be.web.payment.request.PaymentConfirmRequest;
import airdnb.docs.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class PaymentControllerDocs extends RestDocsSupport {

    private final PaymentFacade paymentFacade = mock(PaymentFacade.class);

    @Override
    protected Object initController() {
        return new PaymentController(paymentFacade);
    }

    @DisplayName("예약 결제 임시 데이터 API")
    @Test
    void payment() throws Exception {
        // given
        Long reservationId = 1L;

        given(paymentFacade.addPaymentTemporaryData(any(), any(), any(), any(), any()))
                .willReturn(1L);

        // when then
        mockMvc.perform(
                        post("/payment/reservation/{reservationId}/request", reservationId)
                                .queryParam("paymentKey", "randomValue1")
                                .queryParam("amount", "30000")
                                .queryParam("orderId", "randomValue2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "dn2kqnfv")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("/payment/payment-temporary-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reservationId").attributes(key("url").value("/payment/reservation/{reservationId}/request")).description("결제할 예약 ID")
                        ),
                        queryParameters(
                                parameterWithName("paymentKey").description("Toss Payments 에게 받은 고유 결제 키"),
                                parameterWithName("orderId").description("Toss Payments 에게 받은 고유 주문 번호"),
                                parameterWithName("amount").description("결제할 예약에 대한 총 금액")
                        ),
                        requestCookies(
                                cookieWithName("JSESSIONID").description("로그인 세션 쿠키")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("결제 임시 데이터 ID")
                        )));
    }

    @DisplayName("예약 결제 승인 API")
    @Test
    void confirmPaymentByReservation() throws Exception {
        // given
        Long paymentTemporaryId = 1L;
        String paymentKey = "paymentKey_123xth43";
        String amount = "50000";
        String orderId = "orderId_12323xth43";

        PaymentConfirmRequest request = createPaymentConfirmRequest(paymentKey, amount, orderId);

        PaymentReservationResponse response = createPaymentReservationResponse(paymentKey, amount, orderId);
        given(paymentFacade.confirmPaymentByReservation(any()))
                .willReturn(response);

        // when then
        mockMvc.perform(
                        post("/payment/{paymentTemporaryId}/confirm", paymentTemporaryId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .sessionAttr(LOGIN_MEMBER, 1L)
                                .cookie(new Cookie("JSESSIONID", "321e332")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(document("/payment/payment-confirm-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("paymentTemporaryId").attributes(key("url").value("/payment/{paymentTemporaryId}/confirm")).description("결제 임시 데이터 ID")
                        ),
                        requestCookies(
                                cookieWithName("JSESSIONID").description("로그인 세션 쿠키")
                        ),
                        requestFields(
                                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("결제 승인 받을 예약 ID"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("토스에게 받은 결제 키"),
                                fieldWithPath("amount").type(JsonFieldType.STRING).description("토스에게 받은 금액"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("토스에게 받은 주문 ID")
                        ),
                        responseFields(
                                beneathPath("data"),
                                fieldWithPath("paymentConfirmResponse.tossPaymentConfirmId").type(JsonFieldType.NUMBER).description("결제 승인 ID"),
                                fieldWithPath("paymentConfirmResponse.orderId").type(JsonFieldType.STRING).description("결제 승인된 토스에게 받은 주문 ID"),
                                fieldWithPath("paymentConfirmResponse.orderName").type(JsonFieldType.STRING).description("결제 승인된 주문 이름"),
                                fieldWithPath("paymentConfirmResponse.requestedAt").type(JsonFieldType.STRING).description("결제 승인 요청 시각"),
                                fieldWithPath("reservationResponse.reservationId").type(JsonFieldType.NUMBER).description("예약 ID"),
                                fieldWithPath("reservationResponse.stayId").type(JsonFieldType.NUMBER).description("숙소 ID"),
                                fieldWithPath("reservationResponse.guestId").type(JsonFieldType.NUMBER).description("예약한 사람 ID"),
                                fieldWithPath("reservationResponse.checkIn").type(JsonFieldType.STRING).attributes(getDateTimeFormat()).description("체크인 날짜/시간"),
                                fieldWithPath("reservationResponse.checkOut").type(JsonFieldType.STRING).attributes(getDateTimeFormat()).description("체크아웃 날짜/시간"),
                                fieldWithPath("reservationResponse.totalFee").type(JsonFieldType.NUMBER).description("예약 총 요금"),
                                fieldWithPath("reservationResponse.guestCount").type(JsonFieldType.NUMBER).description("숙소 사용 인원 수")
                        )));
    }

    private PaymentConfirmRequest createPaymentConfirmRequest(String paymentKey, String amount, String orderId) {
        return new PaymentConfirmRequest(
                1L,
                paymentKey,
                amount,
                orderId
        );
    }

    private PaymentReservationResponse createPaymentReservationResponse(String paymentKey, String amount, String orderId) {
        return new PaymentReservationResponse(
                new PaymentConfirmResponse(
                        1L,
                        orderId,
                        "orderName_예약 1건",
                        "requestedAt_결제요청시간"
                ),
                new ReservationResponse(
                        1L,
                        1L,
                        1L,
                        LocalDateTime.of(2024, 8, 13, 15, 0),
                        LocalDateTime.of(2024, 8, 15, 11, 0),
                        new BigDecimal(amount),
                        3
                ));
    }
}
