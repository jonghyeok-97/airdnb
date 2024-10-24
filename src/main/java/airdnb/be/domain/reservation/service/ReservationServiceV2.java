package airdnb.be.domain.reservation.service;

import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.embedded.ReservationStatus;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.entity.ReservationDate;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceV2 {

    private final ReservationRepository reservationRepository;
    private final ReservationDateRepository reservationDateRepository;

    // 결제 시 사용하는 예약 프로세스
    @Transactional
    public ReservationResponse confirmReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(r -> r.isCreatedBy(memberId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_CONFIRM_RESERVATION));

        List<ReservationDate> dates = ReservationDate.of(
                reservation.getStayId(),
                reservation.getCheckIn().toLocalDate(),
                reservation.getCheckOut().toLocalDate()
        );

        if (isReserved(dates, reservation.getStayId())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_RESERVATION);
        }

        reservationDateRepository.saveAll(dates);
        reservation.updateStatus(ReservationStatus.RESERVED);

        return ReservationResponse.from(reservation);
    }

    private boolean isReserved(List<ReservationDate> dates, Long stayId) {
        List<ReservationDate> reservedDates = reservationDateRepository.findReservationDatesByStayId(stayId);

        return reservedDates.stream()
                .anyMatch(dates::contains);
    }
}
