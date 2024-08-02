package airdnb.be.member.service;

import airdnb.be.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean existsMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
