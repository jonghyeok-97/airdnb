package airdnb.be.member.service;

import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.member.MemberRepository;
import airdnb.be.member.controller.request.MemberRequest;
import airdnb.be.member.entitiy.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean existsMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void addMember(MemberRequest request) {
        boolean existsMember = existsMemberByEmail(request.email());
        if (existsMember) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_MEMBER);
        }
        Member member = request.toMember();
        memberRepository.save(member);
    }
}
