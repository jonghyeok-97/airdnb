package airdnb.be.domain.review;

import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.review.entity.Review;
import airdnb.be.domain.review.request.StayReviewAddServiceRequest;
import airdnb.be.domain.review.response.ReviewResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReviewResponse addStayReview(Long memberId, Long stayId, LocalDateTime now, StayReviewAddServiceRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESERVATION));

        if (isEndedReservation(memberId, stayId, now, reservation)) {
            Review saved = reviewRepository.save(new Review(memberId, request.content(), request.starRating()));
            return ReviewResponse.of(saved);
        }
        throw new BusinessException(ErrorCode.RESERVATION_NOT_ENDED);
    }

    // 숙소를 이용한 회원이 맞는지 확인, 해당 API요청이 checkout 이후에 들어왔는지 확인
    private boolean isEndedReservation(Long memberId, Long stayId, LocalDateTime now, Reservation reservation) {
        return reservation.isCreatedBy(memberId, stayId) && reservation.isEnd(now);
    }
}
