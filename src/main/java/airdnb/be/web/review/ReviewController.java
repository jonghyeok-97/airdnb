package airdnb.be.web.review;

import airdnb.be.annotation.argumentResolver.Login;
import airdnb.be.domain.review.ReviewService;
import airdnb.be.domain.review.response.ReviewResponse;
import airdnb.be.web.ApiResponse;
import airdnb.be.web.review.request.StayReviewAddRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
        return ApiResponse.ok(
                reviewService.addStayReview(memberId, stayId, LocalDateTime.now(), request.toServiceRequest())
        );
    }
}
