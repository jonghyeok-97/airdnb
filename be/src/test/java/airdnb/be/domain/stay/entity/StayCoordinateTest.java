package airdnb.be.domain.stay.entity;

import static airdnb.be.domain.stay.entity.StayCoordinate.LATITUDE_ERR_MSG;
import static airdnb.be.domain.stay.entity.StayCoordinate.LONGITUDE_ERR_MSG;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StayCoordinateTest {

    @DisplayName("경도(X좌표)의 범위인 -180도 ~ 180도를 벗어나면 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource({"-181, -180.00001", "180.0000001", "1004"})
    void overLongitudeRangeOccurException(double longitude) {
        // given
        double latitude = 37.4;

        // when then
        assertThatThrownBy(() -> new StayCoordinate(longitude, latitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LONGITUDE_ERR_MSG);
    }

    @DisplayName("경도(X좌표)의 범위는 -180도 ~ 180도 이다.")
    @ParameterizedTest
    @CsvSource({"-180.0000", "180.000000"})
    void longitudeRange(double longitude) {
        // given
        double latitude = 37.4;

        // when then
        assertThatCode(() -> new StayCoordinate(longitude, latitude))
                .doesNotThrowAnyException();
    }

    @DisplayName("위도(Y좌표)의 범위인 -90도 ~ 90도를 벗어나면 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource({"-91, -90.00001", "90.0000001", "100"})
    void overLatitudeRangeOccurException(double latitude) {
        // given
        double longitude = 37.4;

        // when then
        assertThatThrownBy(() -> new StayCoordinate(longitude, latitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LATITUDE_ERR_MSG);
    }

    @DisplayName("위도(Y좌표)의 범위는 -90도 ~ 90도 이다.")
    @ParameterizedTest
    @CsvSource({"-90.0000", "90.000000"})
    void latitudeRange(double latitude) {
        // given
        double longitude = 37.4;

        // when then
        assertThatCode(() -> new StayCoordinate(longitude, latitude))
                .doesNotThrowAnyException();
    }
}