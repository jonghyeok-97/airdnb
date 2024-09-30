package airdnb.be.domain.review.response;

public record ReviewResponse(
        long reviewId,
        long writerId,
        String content,
        double starRating
) {
}
