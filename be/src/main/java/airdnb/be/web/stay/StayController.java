package airdnb.be.web.stay;

import airdnb.be.domain.stay.StayService;
import airdnb.be.web.stay.request.StayAddRequest;
import airdnb.be.web.stay.response.StayResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void addStay(@RequestBody @Valid StayAddRequest request) {
        stayService.addStay(request.toStay());
    }

    @GetMapping({"/{stayId}"})
    public StayResponse getStay(@PathVariable Long stayId) {
        return StayResponse.from(stayService.getStay(stayId));
    }
}
