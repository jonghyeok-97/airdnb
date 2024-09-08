package airdnb.be.domain.stay.service.response;

import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.entity.StayImage;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record StayResponse(

        Long stayId,
        Long memberId,
        String title,
        String description,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        BigDecimal feePerNight,
        int guestCount,
        double longitude,
        double latitude,
        List<String> imageUrls

) {
    public static StayResponse from(Stay stay) {
        return new StayResponse(
                stay.getStayId(),
                stay.getMemberId(),
                stay.getTitle(),
                stay.getDescription(),
                stay.getCheckInTime(),
                stay.getCheckOutTime(),
                stay.getFeePerNight(),
                stay.getGuestCount(),
                stay.getStayCoordinate().getX(),
                stay.getStayCoordinate().getY(),
                stay.getStayImages().stream()
                        .map(StayImage::getUrl)
                        .toList()
        );
    }
}
