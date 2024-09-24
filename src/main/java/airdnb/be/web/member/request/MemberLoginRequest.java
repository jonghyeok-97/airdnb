package airdnb.be.web.member.request;

import airdnb.be.annotation.validation.ValidEmail;
import airdnb.be.domain.member.service.request.MemberLoginServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MemberLoginRequest(

        @ValidEmail(message = "유효한 이메일이 아닙니다.")
        @NotNull
        String email,

        @NotEmpty
        String password

) {
        public MemberLoginServiceRequest toServiceRequest() {
                return new MemberLoginServiceRequest(email, password);
        }
}
