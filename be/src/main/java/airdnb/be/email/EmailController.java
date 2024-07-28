package airdnb.be.email;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email/check")
    public void add(@RequestBody EmailVerificationDto emailDto) {
        emailService.sendVerificationEmail(emailDto.email());
    }
}
