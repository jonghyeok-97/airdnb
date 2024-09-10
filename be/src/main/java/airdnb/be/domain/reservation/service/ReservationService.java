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
import airdnb.be.web.reservation.response.ReservationResponse;
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
        // 예약하는 사람이 회원가입 했는지 확인
        memberRepository.findById(request.guestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER));

        // 숙소 있는지 확인
        Stay stay = stayRepository.findById(request.stayId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_STAY));

        // 예약 생성
        Reservation reservation = stay.createReservation(
                request.guestId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.guestCount()
        );

        // 예약될 날짜들 생성
        List<ReservationDate> reservationDates = reservation.createReservationDate();

        // 해당 숙소에 예약이 가능한지 체크
        List<ReservationDate> reservedDates = reservationDateRepository.findReservationDatesByStayId(
                stay.getStayId());

        boolean isReserved = reservationDates.stream()
                .anyMatch(reservedDates::contains);

        // 예약하려는 날짜에 예약이 되어 있다면
        if (isReserved) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_RESERVATION);
        }

        // 예약하려는 날짜에 예약이 되어있지 않다면
        reservationDateRepository.saveAll(reservationDates);
        Reservation saved = reservationRepository.save(reservation);

        return ReservationResponse.from(saved);
    }
}
