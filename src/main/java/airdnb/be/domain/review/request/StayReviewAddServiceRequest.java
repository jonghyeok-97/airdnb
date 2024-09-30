package airdnb.be.domain.review.request;

public record StayReviewAddServiceRequest(

        Long reservationId,
        String content,
        Double starRating
) {
}
