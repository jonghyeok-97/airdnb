package airdnb.be.domain.stay.embedded;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StayImageTest {

    @DisplayName("")
    @ParameterizedTest
    @CsvSource(value = {"url1,url1,true","url1,url2,false"})
    void equalsAndHashCode(String url1, String url2, boolean result) {
        // given
        StayImage stayImage1 = new StayImage(url1);
        StayImage stayImage2 = new StayImage(url2);

        // when
        boolean expected = stayImage1.equals(stayImage2);

        // then
        assertThat(result).isEqualTo(expected);
    }
}