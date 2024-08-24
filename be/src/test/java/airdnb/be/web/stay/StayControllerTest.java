package airdnb.be.web.stay;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.stay.service.StayService;
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
@WebMvcTest(StayController.class)
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
        );

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isOk());
    }
}