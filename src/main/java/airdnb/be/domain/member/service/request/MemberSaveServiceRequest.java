package airdnb.be.domain.member.service.request;

import airdnb.be.domain.member.entitiy.Member;

public record MemberSaveServiceRequest(

        String name,
        String email,
        String phoneNumber,
        String password

) {
    public Member toMember() {
        return new Member(name, email, phoneNumber, password);
    }
}

