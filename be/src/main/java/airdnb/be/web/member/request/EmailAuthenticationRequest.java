package airdnb.be.web.member.request;

import jakarta.validation.constraints.NotBlank;

public record EmailAuthenticationRequest(

        @NotBlank
        String email,

        @NotBlank
        String authCode
) {
}
