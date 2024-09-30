package airdnb.be.web.review.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record StayReviewAddRequest(

        @NotBlank(message = "리뷰 내용을 입력해 주세요")
        String content,

        @NotNull
        @Range(max = 5, message = "별점은 0~5점 사이입니다")
        Double starRating
) {
}
