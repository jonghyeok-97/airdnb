package airdnb.be.web.stay;

import airdnb.be.api.ApiResponse;
import airdnb.be.domain.stay.service.StayService;
import airdnb.be.web.stay.request.StayAddRequest;
import airdnb.be.domain.stay.service.response.StayResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ApiResponse<Long> addStay(@RequestBody @Valid StayAddRequest request) {

        Long stayId = stayService.addStay(request.toStay());
        return ApiResponse.ok(stayId);
    }

    @GetMapping({"/{stayId}"})
    public StayResponse getStay(@PathVariable Long stayId) {
        return stayService.getStay(stayId);
    }

    @DeleteMapping("/{stayId}")
    public void deleteStay(@PathVariable Long stayId) {
        stayService.deleteStay(stayId);
    }
}
