package airdnb.be.domain.review.response;

import airdnb.be.domain.review.entity.Review;

public record ReviewResponse(
        long reviewId,
        long writerId,
        String content,
        double starRating
) {
    public static ReviewResponse of(Review saved) {
        return new ReviewResponse(
                saved.getReviewId(),
                saved.getMemberId(),
                saved.getContent(),
                saved.getStarRating()
        );
    }
}
