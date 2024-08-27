package airdnb.be.web.stay;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.stay.service.StayService;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.web.stay.request.StayAddRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles(value = "test")
@WebMvcTest(controllers = StayController.class)
class StayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StayService stayService;

    @DisplayName("신규 숙소를 등록한다.")
    @Test
    void createStay() throws Exception {
        // given
        StayAddRequest stayAddRequest = createStayAddRequest(1L);

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @DisplayName("숙소를 조회한다.")
    @Test
    void getStay() throws Exception {
        //given
        StayResponse stayResponse = null;
        given(stayService.getStay(1L))
                .willReturn(stayResponse);

        // when then
        mockMvc.perform(
                        get("/stay/1")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("숙소를 삭제한다.")
    @Test
    void deleteStay() throws Exception {
        //given
        Long targetId = 3L;

        doNothing()
                .when(stayService)
                .deleteStay(targetId);

        // when then
        mockMvc.perform(
                        get("/stay" + "/" + targetId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private StayAddRequest createStayAddRequest(Long memberId) {
        return new StayAddRequest(
                memberId,
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
}