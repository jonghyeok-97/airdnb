package airdnb.be.domain.member.service.request;

public record MemberLoginServiceRequest(

        String email,
        String password

) {
}
