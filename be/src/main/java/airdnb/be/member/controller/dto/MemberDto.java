package airdnb.be.member.controller.dto;

import airdnb.be.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record MemberDto(

        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @Pattern(regexp = "^010-\\d{4}-\\d{4}$")
        @NotNull
        String phoneNumber
) {
    public Member toMember() {
        return new Member(name, email, phoneNumber);
    }
}
