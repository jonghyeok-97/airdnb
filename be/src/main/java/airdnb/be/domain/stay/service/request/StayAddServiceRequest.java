package airdnb.be.domain.stay.service.request;

import airdnb.be.domain.stay.entity.Stay;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record StayAddServiceRequest(

        Long memberId,
        String title,
        String description,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        BigDecimal feePerNight,
        Integer guestCount,
        Double longitude, // 경도 세로선 X좌표
        Double latitude,  // 위도 가로선 Y좌표
        List<String> images

) {
    public Stay toStay() {
        return new Stay(memberId, title, description, checkInTime, checkOutTime, feePerNight, guestCount, longitude, latitude);
    }
}
