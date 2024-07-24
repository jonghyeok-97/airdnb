package airdnb.be.member;

import airdnb.be.member.controller.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void add(MemberDto dto) {
        memberRepository.save(dto.toMember());
    }
}
