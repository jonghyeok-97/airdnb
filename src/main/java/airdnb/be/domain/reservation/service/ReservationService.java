package airdnb.be.domain.reservation.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.entity.ReservationDate;
import airdnb.be.domain.reservation.service.request.ReservationAddServiceRequest;
import airdnb.be.domain.reservation.service.response.ReservationResponse;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
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

        // 예약할 날짜 생성
        List<ReservationDate> reservationDates = ReservationDate.of(
                request.stayId(),
                request.checkInDate(),
                request.checkOutDate());

        // 예약할 날짜에 이미 예약이 되어 있다면 예약 불가
        if (isReserved(reservationDates, stay)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_RESERVATION);
        }

        // 예약 생성
        Reservation reservation = stay.createReservation(
                request.guestId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.guestCount()
        );

        // 예약과 예약날짜 저장
        reservationDateRepository.saveAll(reservationDates);
        Reservation saved = reservationRepository.save(reservation);

        return ReservationResponse.from(saved);
    }

    private boolean isReserved(List<ReservationDate> dates, Stay stay) {
        List<ReservationDate> reservedDates = reservationDateRepository.findReservationDatesByStayId(stay.getStayId());

        return reservedDates.stream()
                .anyMatch(dates::contains);
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
