package airdnb.be.domain.stay.embedded;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class StayCoordinate {

    public static final String LONGITUDE_ERR_MSG = "경도의 범위는 -180도 180도 사이입니다.";
    public static final String LATITUDE_ERR_MSG = "위도의 범위는 -90도 90도 사이입니다.";

    private static final int LON_LAN_SRID  = 4326; // X좌표 경도, Y좌표 위도
    private static final int MAX_LONGITUDE = 180;
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LATITUDE = 90;
    private static final int MIN_LATITUDE = -90;

    private Point point;

    public StayCoordinate(double longitude, double latitude) {
        validateLongitudeRange(longitude);
        validateLatitudeRange(latitude);
        point = createPoint(longitude, latitude);
    }

    private Point createPoint(double longitude, double latitude) {
        GeometryFactory factory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = factory.createPoint(coordinate);
        point.setSRID(LON_LAN_SRID);

        return point;
    }

    private void validateLongitudeRange(double longitude) {
        if (!(MIN_LONGITUDE <= longitude && longitude <= MAX_LONGITUDE)) {
            throw new IllegalArgumentException(LONGITUDE_ERR_MSG);
        }
    }

    private void validateLatitudeRange(double latitude) {
        if (!(MIN_LATITUDE <= latitude && latitude <= MAX_LATITUDE)) {
            throw new IllegalArgumentException(LATITUDE_ERR_MSG);
        }
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }
}
