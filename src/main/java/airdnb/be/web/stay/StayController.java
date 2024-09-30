package airdnb.be.web.stay;

import airdnb.be.annotation.argumentResolver.Login;
import airdnb.be.domain.stay.service.StayService;
import airdnb.be.domain.stay.service.response.StayReservedDatesResponse;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.web.ApiResponse;
import airdnb.be.web.stay.request.StayAddRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stay")
public class StayController {

    private final StayService stayService;

    @PostMapping
    public ApiResponse<Long> addStay(@Login Long memberId,
                                     @RequestBody @Valid StayAddRequest request) {
        Long stayId = stayService.addStay(request.toServiceRequest(memberId));
        return ApiResponse.ok(stayId);
    }

    @GetMapping({"/{stayId}"})
    public ApiResponse<StayResponse> getStay(@PathVariable Long stayId) {
        return ApiResponse.ok(stayService.getStay(stayId));
    }

    @DeleteMapping("/{stayId}")
    public ApiResponse<Void> deleteStay(@Login Long memberId, @PathVariable Long stayId) {
        stayService.deleteStay(stayId);
        return ApiResponse.ok();
    }

    /**
     * put : 전체 리소스 변경, 멱등
     * patch : 부분 리소스 변경, 구현에 따라 멱등x
     */
    @PutMapping("/{stayId}/image")
    public ApiResponse<StayResponse> changeStayImage(
            @Login Long memberId, @PathVariable Long stayId, @RequestBody List<String> imageUrls) {
        StayResponse stayResponse = stayService.changeStayImage(stayId, imageUrls);
        return ApiResponse.ok(stayResponse);
    }

    @GetMapping("/{stayId}/reservedDates")
    public ApiResponse<StayReservedDatesResponse> getReservedDates(@PathVariable Long stayId) {
        return ApiResponse.ok(stayService.getReservedDates(stayId));
    }
}
