package airdnb.be.web.member.request;

import airdnb.be.annotation.validation.ValidEmail;
import airdnb.be.domain.mail.request.EmailAuthenticationServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailAuthenticationRequest(

        @ValidEmail(message = "유효한 이메일이 아닙니다.")
        @NotNull
        String email,

        @NotBlank
        String authCode
) {
        public EmailAuthenticationServiceRequest toServiceRequest() {
                return new EmailAuthenticationServiceRequest(email, authCode);
        }
}
