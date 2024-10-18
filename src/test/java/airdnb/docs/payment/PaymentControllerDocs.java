package airdnb.docs.payment;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.payment.PaymentService;
import airdnb.be.web.payment.PaymentController;
import airdnb.docs.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class PaymentControllerDocs extends RestDocsSupport {

    private final PaymentService paymentService = mock(PaymentService.class);

    @Override
    protected Object initController() {
        return new PaymentController(paymentService);
    }

    @DisplayName("예약 결제 임시 데이터 API")
    @Test
    void payment() throws Exception {
        // given
        mockMvc.perform(
                        post("/payment/reservation/request")
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
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("/payment/payment-temporary-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("paymentKey").description("Toss Payments 에게 받은 고유 결제 키"),
                                parameterWithName("orderId").description("Toss Payments 에게 받은 고유 주문 번호"),
                                parameterWithName("amount").description("결제할 예약에 대한 총 금액")
                        ),
                        requestCookies(
                                cookieWithName("JSESSIONID").description("로그인 세션 쿠키")
                        )));
    }
}
