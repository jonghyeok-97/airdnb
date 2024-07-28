package airdnb.be.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailVerificationDto(

        @NotNull
        @Email(regexp = "^[^@]+@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
        String email
) {}
