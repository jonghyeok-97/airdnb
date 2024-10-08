package airdnb.be.domain.stay.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.reservation.ReservationDateRepository;
import airdnb.be.domain.reservation.entity.ReservationDate;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.service.request.StayAddServiceRequest;
import airdnb.be.domain.stay.service.response.StayReservedDatesResponse;
import airdnb.be.domain.stay.service.response.StayResponse;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StayService {

    private final StayRepository stayRepository;
    private final MemberRepository memberRepository;
    private final ReservationDateRepository reservationDateRepository;

    @Transactional
    public Long addStay(StayAddServiceRequest request) {
        Stay stay = request.toStay();

        memberRepository.findById(stay.getHostId())
                .orElseThrow(() -> {
                    log.warn("'{}' 은 존재하지 않는 회원입니다.", stay.getHostId());
                    return new BusinessException(ErrorCode.NOT_EXIST_MEMBER);
                });

        return stayRepository.save(stay).getStayId();
    }

    public StayResponse getStay(Long stayId) {
        return stayRepository.findById(stayId)
                .map(StayResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_STAY));
    }

    @Transactional
    public void deleteStay(Long stayId) {
        stayRepository.deleteById(stayId);
    }

    @Transactional
    public StayResponse changeStayImage(Long stayId, List<String> images) {
        Stay stay = stayRepository.findById(stayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_STAY));

        stay.changeStayImages(images);
        return StayResponse.from(stay);
    }

    public StayReservedDatesResponse getReservedDates(Long stayId) {
        List<LocalDate> reservedDates = reservationDateRepository.findReservationDatesByStayId(stayId).stream()
                .map(ReservationDate::getReservationDate)
                .toList();

        return new StayReservedDatesResponse(reservedDates);
    }
}
