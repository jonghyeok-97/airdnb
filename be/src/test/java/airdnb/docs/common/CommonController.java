package airdnb.docs.common;

import airdnb.be.exception.ErrorCode;
import airdnb.be.web.ApiResponse;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("/docs")
    public ApiResponse<Void> docsCommon(){
        return ApiResponse.ok();
    }

    @GetMapping("/error")
    public List<ApiResponse<Object>> docsError(){
        return Stream.of(ErrorCode.values())
                .map(errorCode -> ApiResponse.errorCode(errorCode))
                .toList();
    }
}
