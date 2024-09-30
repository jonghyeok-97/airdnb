package airdnb.be.domain.reservation.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.entity.ReservationDate;
import airdnb.be.domain.reservation.service.request.ReservationAddServiceRequest;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final MemberRepository memberRepository;
    private final StayRepository stayRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationDateRepository reservationDateRepository;

    @Transactional
    public ReservationResponse reserve(ReservationAddServiceRequest request) {
        // 예약하는 사람과 숙소 있는지 확인
        checkExistMember(request.guestId());
        Stay stay = checkExistStay(request.stayId());

        // 예약 생성
        Reservation reservation = stay.createReservation(
                request.guestId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.guestCount()
        );

        // 예약될 날짜들 생성
        Reservation saved = reservationRepository.save(reservation);
        List<ReservationDate> reservationDates = saved.createReservationDate();

        // 예약하려는 날짜에 예약이 되어 있다면 예외 발생
        if (isReserved(reservationDates, stay)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_RESERVATION);
        }

        // 예약하려는 날짜에 예약이 되어있지 않다면 예약 성공
        reservationDateRepository.saveAll(reservationDates);

        return ReservationResponse.from(saved);
    }

    private boolean isReserved(List<ReservationDate> reservationDates, Stay stay) {
        List<ReservationDate> reservedDates = reservationDateRepository.findReservationDatesByStayId(stay.getStayId());

        return reservationDates.stream()
                .anyMatch(reservedDates::contains);
    }

    private Stay checkExistStay(Long stayId) {
        return stayRepository.findById(stayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_STAY));
    }

    private void checkExistMember(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER));
    }
}
