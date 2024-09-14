package airdnb.be.web.member.request;

import airdnb.be.annotation.validation.ValidEmail;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(

        @ValidEmail(message = "유효한 이메일이 아닙니다.")
        @NotNull
        String email

) {
}
