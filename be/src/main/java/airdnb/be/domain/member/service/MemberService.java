package airdnb.be.domain.member.service;

import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // email 존재 여부로 회원가입인지, 로그인인지 판단할 떄 사용
    public boolean existsMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void addMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_MEMBER);
        }
        memberRepository.save(member);
    }

    public boolean login(String email, String password) {
        return Optional.ofNullable(memberRepository.findMemberByEmail(email))
                .map(member -> member.hasPassword(password))
                .orElse(false);
    }
}
