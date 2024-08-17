package airdnb.be.web.stay.request;

import airdnb.be.domain.stay.entity.Stay;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.hibernate.validator.constraints.Range;

public record StayAddRequest(

        @NotNull
        Long memberId,

        @NotNull
        String title,

        String description,

        @NotNull
        LocalTime checkInTime,

        @NotNull
        LocalTime checkOutTime,

        @NotNull
        @Positive
        BigDecimal feePerNight,

        @NotNull
        @Positive
        Integer guestCount,

        @NotNull
        @Range(min = -180, max = 180)
        Double longitude, // 경도 세로선 X좌표

        @NotNull
        @Range(min = -90, max = 90)
        Double latitude,  // 위도 가로선 Y좌표

        @NotNull
        @Size(min = 5)
        List<String> images
) {
        public Stay toStay() {
                return new Stay(memberId, title, description, checkInTime, checkOutTime, feePerNight, guestCount, longitude, latitude, images);
        }
}
