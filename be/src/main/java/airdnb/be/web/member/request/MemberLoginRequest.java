package airdnb.be.web.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MemberLoginRequest(

        @NotNull
        @Email
        String email,

        @NotEmpty
        String password
) {}
