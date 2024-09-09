package airdnb.be.domain.stay.entity;

import airdnb.be.domain.base.entity.BaseTimeEntity;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.domain.reservation.entity.Reservation;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stayId;

    @Column(nullable = false)
    private Long hostId;

    private String title;

    private String description;

    @Column(nullable = false)
    private LocalTime checkInTime;

    @Column(nullable = false)
    private LocalTime checkOutTime;

    @Column(nullable = false)
    private BigDecimal feePerNight;

    @Column(nullable = false)
    private int guestCount;

    @Column(nullable = false)
    @Embedded
    private StayCoordinate stayCoordinate;

    /**
     * 값 컬렉션 (선택)
     * 숙소 저장 : insert쿼리 5개 ok
     * 업데이트 : 숙소 1개에 해당하는 5개 이미지 삭제, 6개 이미지 insert
     * 이미지의 경로를 추적할 필요성 못느낌
     *
     * OneToMany
     * 숙소 저장 : insert쿼리 5개, update 쿼리 5개 -> 이상하다고 판단
     * 업데이트 : insert 6쿼리, update 6쿼리
     *
     * OneToMany 대안인 양방향 -> 숙소 이미지에서 숙소를 조회할 필요 없는데 양방향을 하면 복잡성만 증가시킴
     *
     * 분리 -> 숙소이미지는 숙소ID를 통해 조회된다는 점 & 숙소와 이미지는 동시에 생성됨.
     */
    @ElementCollection
    @CollectionTable(name = "stay_image",
            joinColumns = @JoinColumn(name = "stay_id"),
            foreignKey = @ForeignKey(name = "stay_id"))
    private List<StayImage> stayImages = new ArrayList<>();

    public Stay(Long hostId, String title, String description, LocalTime checkInTime, LocalTime checkOutTime,
                BigDecimal feePerNight, int guestCount, double longitude, double latitude, List<String> images) {
        this.hostId = hostId;
        this.title = title;
        this.description = description;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.feePerNight = feePerNight;
        this.guestCount = guestCount;
        this.stayCoordinate = new StayCoordinate(longitude, latitude);
        this.stayImages = images.stream()
                .map(StayImage::new)
                .collect(Collectors.toList());
    }

    public Reservation createReservation(Long guestId, LocalDate checkInDate, LocalDate checkOutDate, int guestCount) {
        validateCapable(guestCount);
        return new Reservation(
                this.stayId,
                guestId,
                LocalDateTime.of(checkInDate, checkInTime),
                LocalDateTime.of(checkOutDate, checkOutTime),
                guestCount,
                calculateTotalFee(checkInDate, checkOutDate));
    }

    private void validateCapable(int guestCount) {
        if (this.guestCount < guestCount) {
            throw new BusinessException(ErrorCode.CANNOT_CAPABLE_GUEST);
        }
    }

    private BigDecimal calculateTotalFee(LocalDate checkInDate, LocalDate checkOutDate) {
        long perNightCount = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        return feePerNight.multiply(BigDecimal.valueOf(perNightCount));
    }

    public void changeStayImages(List<String> images) {
        stayImages = images.stream()
                .map(StayImage::new)
                .collect(Collectors.toList());
    }
}
