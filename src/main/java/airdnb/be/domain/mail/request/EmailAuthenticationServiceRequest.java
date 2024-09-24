package airdnb.be.domain.mail.request;

public record EmailAuthenticationServiceRequest(

        String email,
        String authenticationCode

) {
}
