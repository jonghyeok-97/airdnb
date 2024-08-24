package airdnb.be.domain.stay.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.service.request.StayAddServiceRequest;
import airdnb.be.domain.stay.service.response.StayResponse;
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
public class StayService {

    private final StayRepository stayRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long addStay(StayAddServiceRequest serviceRequest) {
        Stay stay = serviceRequest.toStay();

        memberRepository.findById(stay.getMemberId())
                .orElseThrow(() -> {
                    log.warn("'{}' 은 존재하지 않는 회원입니다.", stay.getMemberId());
                    return new BusinessException(ErrorCode.NOT_EXIST_MEMBER);
                });
        Stay saved = stayRepository.save(stay);

        return saved.getStayId();
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
}
