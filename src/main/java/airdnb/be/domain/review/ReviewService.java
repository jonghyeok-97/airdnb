package airdnb.be.domain.review;

import airdnb.be.domain.review.request.StayReviewAddServiceRequest;
import airdnb.be.domain.review.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    public ReviewResponse addStayReview(Long memberId, Long stayId, StayReviewAddServiceRequest request) {

        return null;
    }
}
