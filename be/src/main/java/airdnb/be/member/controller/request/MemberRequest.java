package airdnb.be.member.controller.request;

import airdnb.be.member.entitiy.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record MemberRequest(

        @NotBlank
        String name,

        @Email(regexp = "^[^@]+@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
        @NotNull
        String email,

        @Pattern(regexp = "^010-\\d{4}-\\d{4}$")
        @NotNull
        String phoneNumber
) {
    public Member toMember() {
        return new Member(name, email, phoneNumber);
    }
}
