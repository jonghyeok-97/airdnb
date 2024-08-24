package airdnb.be.web.stay.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.be.domain.stay.service.StayService;
import airdnb.be.web.stay.StayController;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StayController.class)
class StayAddRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StayService stayService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @DisplayName("신규 숙소를 등록할 때 "
            + "회원 식별자, 제목, 체크인시간, 체크아웃시간, 1박당 요금, 숙박 인원수, 위도, 경도, 이미지에는 "
            + "null이 들어갈 수 없다.")
    @Test
    void createStayWithoutNull() throws Exception {
        // given
        StayAddRequest stayAddRequest = new StayAddRequest(
                null,
                null,
                "설명",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // when then
        Set<ConstraintViolation<StayAddRequest>> validate = validator.validate(stayAddRequest);

        assertThat(validate.size()).isEqualTo(9);
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("신규 숙소 등록할 때 제목에는 빈값, 공백만은 들어갈 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void createStayWithoutTitle(String title) throws Exception {
        // given
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                title,
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(30000),
                3,
                104.2,
                58.3,
                List.of("imageUrl1", "url2", "url3", "url4", "url5")
        );

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("제목을 넣어주세요."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 숙소 등록할 때 숙소 이미지가 5개 미만이면 Validator 에 의해 걸러진다.")
    @Test
    void createStayWithImageMoreThanFive() throws Exception {
        // given
        List<String> target = List.of("url1", "url2", "url3", "url4");
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
                target
        );

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("이미지는 최소 5개여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 숙소 등록할 때 1박당 요금은 최소 10000원이다.")
    @ParameterizedTest
    @ValueSource(ints = {9999, 0, -10000})
    void createStayWithFeePerNightMoreThan10000(int feePerNight) throws Exception {
        // given
        BigDecimal target = new BigDecimal(feePerNight);
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                target,
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("1박당 요금은 최소 10000원 입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 숙소 등록할 때 숙박 인원 수는 최소 1명입니다,")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createStayWithGuestCountMoreThan1(int guestCount) throws Exception {
        // given
        int target = guestCount;
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(10000),
                target,
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("숙박 인원 수는 최소 1명입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 숙소 등록할 때 경도(X좌표)가 -180도 미만, 180도 초과면 검증되어진다.")
    @ParameterizedTest
    @ValueSource(doubles = {-180.000001, 180.00000001})
    void createStayWithLongitude(double longitude) throws Exception {
        // given
        double target = longitude;
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(10000),
                1,
                target,
                58.3,
                List.of("url1", "url2", "url3", "url4", "url5")
        );

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("경도는 -180도 이상, 180도 이하입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 숙소 등록할 때 경도(X좌표)가 -180도 이상 180도 이하면 통과이다.")
    @ParameterizedTest
    @ValueSource(doubles = {-180.00000, 0, 180.0000000})
    void createStayWithLongitude2(double longitude) throws Exception {
        // given
        double target = longitude;
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(10000),
                1,
                target,
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

    @DisplayName("신규 숙소 등록할 때 위도(Y좌표)가 -90도 미만, 90도 초과면 검증되어진다.")
    @ParameterizedTest
    @ValueSource(doubles = {-90.000001, 90.00000001})
    void createStayWithLatitude(double latitude) throws Exception {
        // given
        double target = latitude;
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(10000),
                1,
                90.0,
                target,
                List.of("url1", "url2", "url3", "url4", "url5")
        );

        // when then
        mockMvc.perform(
                        post("/stay")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(stayAddRequest))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("위도는 -90도 이상, 90도 이하입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 숙소 등록할 때 위도(Y좌표)가 -90도 이상 90도 이하면 통과이다.")
    @ParameterizedTest
    @ValueSource(doubles = {-90.00000, 0, 90.0000000})
    void createStayWithLatitude2(double latitude) throws Exception {
        // given
        double target = latitude;
        StayAddRequest stayAddRequest = new StayAddRequest(
                1L,
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(10000),
                1,
                90.0,
                target,
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