package airdnb.be.web.stay;

import airdnb.be.domain.stay.StayService;
import airdnb.be.web.stay.request.StayAddRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
