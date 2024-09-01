package airdnb.be.domain.member.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.member.service.request.MemberLoginServiceRequest;
import airdnb.be.domain.member.service.request.MemberSaveServiceRequest;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // email 존재 여부로 회원가입을 해야하는 회원인지, 로그인해야 하는 회원인지 판단할 떄 사용
    public boolean existsMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public Long addMember(MemberSaveServiceRequest request) {
        if (existsMemberByEmail(request.email())) {
            log.warn("message: {}은 이미 회원가입이 되어있습니다", request.email());
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_MEMBER);
        }
        Member saved = memberRepository.save(request.toMember());
        return saved.getId();
    }

    public void login(MemberLoginServiceRequest request) {
        Optional.ofNullable(memberRepository.findMemberByEmail(request.email()))
                .filter(member -> member.hasPassword(request.password()))
                .orElseThrow(() -> {
                    log.warn("'{}'의 로그인 정보가 정확하지 않습니다", request.email());
                    return new BusinessException(ErrorCode.CANNOT_LOGIN);
                });
    }
}
