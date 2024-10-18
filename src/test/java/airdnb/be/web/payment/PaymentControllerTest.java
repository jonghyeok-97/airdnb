package airdnb.be.web.payment;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PaymentControllerTest extends ControllerTestSupport {

    @DisplayName("예약 결제 임시 데이터 추가하면 OK_200 을 응답한다.")
    @Test
    void addPaymentTemporaryData() throws Exception {
        // when then
        mockMvc.perform(
                        post("/payment/reservation/request")
                                .queryParam("paymentKey", "randomValue1")
                                .queryParam("amount", "30000")
                                .queryParam("orderId", "randomValue2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .sessionAttr(LOGIN_MEMBER, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}