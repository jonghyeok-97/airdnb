package airdnb.be.member.service;

import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.member.MemberRepository;
import airdnb.be.member.controller.request.MemberRequest;
import airdnb.be.member.entitiy.Member;
import airdnb.be.utils.RedisUtils;
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
    public void addMember(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_MEMBER);
        }
        Member member = request.toMember();
        memberRepository.save(member);
    }
}
