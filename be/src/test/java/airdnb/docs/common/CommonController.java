package airdnb.docs.common;

import airdnb.be.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("/docs")
    public ApiResponse<Void> docs(){
        return ApiResponse.ok();
    }
}
