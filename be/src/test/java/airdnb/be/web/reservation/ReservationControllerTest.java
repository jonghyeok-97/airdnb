package airdnb.be.web.reservation;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.ControllerTestSupport;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.web.reservation.request.ReservationAddRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class ReservationControllerTest extends ControllerTestSupport {

    @DisplayName("예약이 완료되면 OK_200 을 반환한다")
    @Test
    void reserve() throws Exception {
        // given
        ReservationAddRequest request = new ReservationAddRequest(
                1L,
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
                LocalDateTime.of(2024, 4, 30, 15,0),
                new BigDecimal(50000)
        );

        BDDMockito.given(reservationService.reserve(any()))
                        .willReturn(response);

        // when then
        mockMvc.perform(
                MockMvcRequestBuilders.post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());
    }
}