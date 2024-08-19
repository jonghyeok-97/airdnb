package airdnb.be.domain.stay;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
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
    public void addStay(Stay stay) {
        memberRepository.findById(stay.getMemberId())
                .orElseThrow(() -> {
                    log.warn("'{}' 은 존재하지 않는 회원입니다.", stay.getMemberId());
                    return new BusinessException(ErrorCode.NOT_EXIST_MEMBER);
                });

        stayRepository.save(stay);
    }

    public Stay getStay(Long stayId) {
        return stayRepository.findById(stayId)
                .orElseThrow();
    }

    @Transactional
    public void deleteStay(Long stayId) {
        stayRepository.deleteById(stayId);
    }
}
