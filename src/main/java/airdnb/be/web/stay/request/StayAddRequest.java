package airdnb.be.web.stay.request;

import airdnb.be.domain.stay.service.request.StayAddServiceRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.hibernate.validator.constraints.Range;

public record StayAddRequest(

        @NotBlank(message = "제목을 넣어주세요.")
        String title,

        String description,

        @NotNull(message = "체크인 시간은 필수입니다.")
        LocalTime checkInTime,

        @NotNull(message = "체크아웃 시간은 필수입니다.")
        LocalTime checkOutTime,

        @NotNull
        @Min(value = 10000, message = "1박당 요금은 최소 10000원 입니다.")
        BigDecimal feePerNight,

        @NotNull
        @Min(value = 1, message = "숙박 인원 수는 최소 1명입니다.")
        Integer guestCount,

        @NotNull
        @Range(min = -180, max = 180, message = "경도는 -180도 이상, 180도 이하입니다.")
        Double longitude, // 경도 세로선 X좌표

        @NotNull
        @Range(min = -90, max = 90, message = "위도는 -90도 이상, 90도 이하입니다.")
        Double latitude,  // 위도 가로선 Y좌표

        @NotNull
        @Size(min = 5, message = "이미지는 최소 5개여야 합니다.")
        List<String> images

) {
        public StayAddServiceRequest toServiceRequest(Long memberId) {
                return new StayAddServiceRequest(memberId, title, description, checkInTime, checkOutTime, feePerNight, guestCount, longitude, latitude, images);
        }
}
