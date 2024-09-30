package airdnb.be.domain.review.request;

public record StayReviewAddServiceRequest(

        String content,
        Double starRating
) {
}
