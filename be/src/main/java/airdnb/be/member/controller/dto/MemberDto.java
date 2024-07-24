package airdnb.be.member.controller.dto;

import airdnb.be.member.Member;

public record MemberDto(
        String name,
        String email,
        String phoneNumber
) {
    public Member toMember() {
        return new Member(name, email, phoneNumber);
    }
}
