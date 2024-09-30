package airdnb.be.domain.stay.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.util.List;

public record StayReservedDatesResponse(

        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        List<LocalDate> reservedDates
) {
}
