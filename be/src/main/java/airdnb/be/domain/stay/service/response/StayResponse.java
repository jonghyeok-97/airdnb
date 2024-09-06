package airdnb.be.domain.stay.service.response;

import airdnb.be.domain.stay.entity.Stay;
import java.math.BigDecimal;
import java.time.LocalTime;

public record StayResponse(

        Long stayId,
        Long memberId,
        String title,
        String description,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        BigDecimal feePerNight,
        Integer guestCount,
        Double longitude,
        Double latitude

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
                stay.getStayCoordinate().getY()
        );
    }
}
