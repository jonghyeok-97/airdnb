package airdnb.be.web.member.request;

import airdnb.be.customBeanValid.ValidEmail;
import airdnb.be.domain.member.service.request.MemberSaveServiceRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MemberSaveRequest(

        @NotBlank
        String name,

        @ValidEmail(message = "유효한 이메일이 아닙니다.")
        @NotNull
        String email,

        @Pattern(regexp = "^010-\\d{4}-\\d{4}$")
        @NotNull
        String phoneNumber,

        @NotBlank
        String password
) {
    public MemberSaveServiceRequest toServiceRequest() {
        return new MemberSaveServiceRequest(name, email, phoneNumber, password);
    }
}
