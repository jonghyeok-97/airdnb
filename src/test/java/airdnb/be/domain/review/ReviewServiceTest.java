package airdnb.be.domain.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.embedded.ReservationStatus;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.review.request.StayReviewAddServiceRequest;
import airdnb.be.domain.review.response.ReviewResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
    }

    @DisplayName("숙소 리뷰 작성하려는 사람이 해당 숙소를 예약했고, 예약이 끝났다면 리뷰를 추가할 수 있다.")
    @Test
    void addStayReview() {
        // given
        Reservation reservation = new Reservation(
                3L,
                4L,
                LocalDateTime.of(2024, 9, 1, 15, 0),
                LocalDateTime.of(2024, 9, 5, 11, 0),
                3,
                new BigDecimal(30000)
        );
        reservation.updateStatus(ReservationStatus.RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        StayReviewAddServiceRequest request = new StayReviewAddServiceRequest(
                savedReservation.getReservationId(),
                "내용",
                3.5d
        );

        // when
        ReviewResponse reviewResponse = reviewService.addStayReview(4L, 3L, LocalDateTime.of(2024, 9, 6, 23, 0),
                request);

        // then
        assertThat(reviewResponse.reviewId()).isNotNull();
        assertThat(reviewResponse)
                .extracting("writerId", "content", "starRating")
                .contains(4L, "내용", 3.5d);
    }

    @DisplayName("숙소 리뷰 작성하려는 사람이 해당 숙소를 예약했지만, 예약이 끝나지 않았다면 예외가 발생한다.")
    @Test
    void addStayReviewWithException() {
        // given
        Reservation reservation = new Reservation(
                3L,
                4L,
                LocalDateTime.of(2024, 9, 1, 15, 0),
                LocalDateTime.of(2024, 9, 5, 11, 0),
                3,
                new BigDecimal(30000)
        );
        Reservation savedReservation = reservationRepository.save(reservation);

        StayReviewAddServiceRequest request = new StayReviewAddServiceRequest(
                savedReservation.getReservationId(),
                "내용",
                3.5d
        );

        // when then
        assertThatThrownBy(() -> reviewService.addStayReview(4L, 3L, LocalDateTime.of(2024, 9, 5, 0, 0),
                request))
                .isInstanceOf(BusinessException.class)
                .extracting("ErrorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_ENDED);
    }
}