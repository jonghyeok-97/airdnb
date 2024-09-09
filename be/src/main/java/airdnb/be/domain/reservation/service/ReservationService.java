package airdnb.be.domain.reservation.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.reservation.ReservationRepository;
import airdnb.be.domain.reservation.entity.Reservation;
import airdnb.be.domain.reservation.service.request.ReservationAddServiceRequest;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.web.reservation.response.ReservationResponse;
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

    @Transactional
    public ReservationResponse reserve(ReservationAddServiceRequest request) {
        memberRepository.findById(request.guestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER));

        Stay stay = stayRepository.findById(request.stayId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_STAY));

        Reservation reservation = stay.createReservation(
                request.guestId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.guestCount());

        Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }
}
