package airdnb.be.member.controller.request;

import jakarta.validation.constraints.NotBlank;

public record EmailAuthenticationRequest(

        @NotBlank
        String email,

        @NotBlank
        String authCode
) {
}
