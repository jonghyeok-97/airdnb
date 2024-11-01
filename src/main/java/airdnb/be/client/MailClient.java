package airdnb.be.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailClient {

    private static final String MAIL_SENDER = "spring.mail.username";
    private static final String TITLE = "Airdnb 클론 프로젝트에서 보낸 인증번호 입니다";

    private final Environment env;
    private final JavaMailSender javaMailSender;

    @Async("mailExecutor")
    public void sendAuthenticationMail(String toEmail, String authenticationCode) {
        SimpleMailMessage mail = createMailMessage(toEmail, authenticationCode);
        javaMailSender.send(mail);
        log.info("[메일] 메일 전송={}", toEmail);
    }

    private SimpleMailMessage createMailMessage(String memberEmail, String randomUUID) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(env.getProperty(MAIL_SENDER));
        message.setTo(memberEmail);
        message.setSubject(TITLE);
        message.setText(randomUUID);

        return message;
    }
}
