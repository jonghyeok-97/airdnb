package airdnb.be.web.review;

import airdnb.be.annotation.argumentResolver.Login;
import airdnb.be.domain.review.ReviewService;
import airdnb.be.domain.review.response.ReviewResponse;
import airdnb.be.web.ApiResponse;
import airdnb.be.web.review.request.StayReviewAddRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/stay/{stayId}/review")
    public ApiResponse<ReviewResponse> addStayReview(@Login Long memberId,
                                                     @PathVariable Long stayId,
                                                     @RequestBody @Valid StayReviewAddRequest request) {
        // 회원이 숙소 리뷰를 달려면
        // 해당 회원이 해당 숙소를 이용했는지 점검
        // 회원이 숙소를 이용하기 전과 후를 구분해야함.
        // 예약이 되었는지 확인 예약 상태 - RESERVED, 숙소를 이용한 회원이 맞는지 회원 ID로 확인, 해당 API요청이 예약 날짜 끝났는지 확인
        return ApiResponse.ok(reviewService.addStayReview(request.toServiceRequest()));
    }
}
