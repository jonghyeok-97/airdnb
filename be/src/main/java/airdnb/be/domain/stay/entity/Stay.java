package airdnb.be.domain.stay.entity;

import airdnb.be.domain.base.entity.BaseTimeEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stay extends BaseTimeEntity {

    private static final int LON_LAN_SRID  = 4326; // X좌표 경도, Y좌표 위도

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stayId;

    @Column(nullable = false)
    private Long memberId;

    private String title;

    private String description;

    @Column(nullable = false)
    private LocalTime checkInTime;

    @Column(nullable = false)
    private LocalTime checkOutTime;

    @Column(nullable = false)
    private BigDecimal feePerNight;

    @Column(nullable = false)
    private Integer guestCount;

    @Column(nullable = false)
    private Point point;

    @ElementCollection
    @CollectionTable(
            name = "stay_image",
            joinColumns = @JoinColumn(name = "stayId")
    )
    private List<Image> images = new ArrayList<>();

    public Stay(Long memberId, String title, String description, LocalTime checkInTime, LocalTime checkOutTime,
                BigDecimal feePerNight, Integer guestCount, Double longitude, Double latitude, List<String> images) {
        this.memberId = memberId;
        this.title = title;
        this.description = description;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.feePerNight = feePerNight;
        this.guestCount = guestCount;
        this.point = createPoint(longitude, latitude);
        this.images = createStayImages(images);
    }

    private List<Image> createStayImages(List<String> images) {
        return images.stream()
                .map(Image::new)
                .collect(Collectors.toList());
    }

    private Point createPoint(Double longitude, Double latitude) {
        GeometryFactory factory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = factory.createPoint(coordinate);
        point.setSRID(LON_LAN_SRID);
        return point;
    }
}
