package airdnb.be.domain.member.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
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
    public void addMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            log.warn("message: {}은 이미 회원가입이 되어있습니다", member.getEmail());
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_MEMBER);
        }
        memberRepository.save(member);
    }

    public void login(String email, String password) {
        Optional.ofNullable(memberRepository.findMemberByEmail(email))
                .filter(member -> member.hasPassword(password))
                .orElseThrow(() -> {
                    log.warn("'{}'의 로그인 정보가 정확하지 않습니다", email);
                    return new BusinessException(ErrorCode.CANNOT_LOGIN);
                });
    }
}
