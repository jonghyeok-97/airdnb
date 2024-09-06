package airdnb.be.domain.stay.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.stay.StayImageRepository;
import airdnb.be.domain.stay.StayRepository;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.entity.StayImage;
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
    private final StayImageRepository stayImageRepository;
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

        /**
         * 값 콜렉션 -> 복합키 생성해야함 -> 복합키를 가진 테이블이 인덱스를 잘 탈 수 있을지 모름
         * OneToMany 풀기 -> update 쿼리, 안맞는 것 같음
         * ManyToOne 양방향 풀기 -> 양방향의 장점 객체탐색이 자유롭지만 지금은 아님
         *
         * 결론 : 보수적으로 접근
         */
        List<StayImage> stayImages = StayImage.from(saved.getStayId(), serviceRequest.images());
        stayImageRepository.saveAll(stayImages);
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
