package airdnb.be.web.stay;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.ControllerTestSupport;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.web.stay.request.StayAddRequest;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class StayControllerTest extends ControllerTestSupport {

    @DisplayName("숙소 등록에 성공하면 200_OK 이다.")
    @Test
    void createStay() throws Exception {
        // given
        StayAddRequest stayAddRequest = createStayAddRequest();

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @DisplayName("숙소 1건 조회에 성공하면 200_OK 이다.")
    @Test
    void getStay() throws Exception {
        //given
        Long stayId = 1L;
        StayResponse stayResponse = createStayResponse(stayId);
        given(stayService.getStay(stayId))
                .willReturn(stayResponse);

        // when then
        mockMvc.perform(
                        get("/stay/{stayId}", stayId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());
    }

    @DisplayName("숙소 삭제에 성공하면 200_OK 이다.")
    @Test
    void deleteStay() throws Exception {
        //given
        Long stayId = 3L;

        doNothing()
                .when(stayService)
                .deleteStay(stayId);

        // when then
        mockMvc.perform(
                        delete("/stay/{stayId}", stayId)
                                .sessionAttr(LOGIN_MEMBER, 1L)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("숙소 이미지 업데이트에 성공하면 200_OK 이다.")
    @Test
    void changeStayImage() throws Exception {
        //given
        Long stayId = 3L;
        List<String> imageUrls = List.of();

        StayResponse stayResponse = createStayResponse(stayId);
        given(stayService.changeStayImage(stayId, List.of()))
                .willReturn(stayResponse);

        // when then
        mockMvc.perform(
                        put("/stay/{stayId}/image", stayId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(imageUrls))
                                .sessionAttr(LOGIN_MEMBER, 1L)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());
    }

    private StayAddRequest createStayAddRequest() {
        return new StayAddRequest(
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
    }

    private StayResponse createStayResponse(Long stayId) {
        return new StayResponse(
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
    }
}