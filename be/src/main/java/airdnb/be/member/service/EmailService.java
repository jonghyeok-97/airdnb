package airdnb.be.member.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private static final String MAIE_SENDER = "spring.mail.username";
    private static final String TITLE = "Airdnb 클론 프로젝트에서 보낸 인증번호 입니다";
    private static final int UUID_PREFIX_START = 0;
    private static final int UUID_PREFIX_END = 6;

    private final Environment env;
    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String memberEmail) {
        try {
            SimpleMailMessage message = createSimpleMessage(memberEmail);
            javaMailSender.send(message);
        } catch (MailException ex) {
            log.error("메일 전송 불가 : {}", ex.getMessage());
        }
    }

    private SimpleMailMessage createSimpleMessage(String memberEmail) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(env.getProperty(MAIE_SENDER));
        message.setTo(memberEmail);
        message.setSubject(TITLE);
        message.setText(createRandomUUID());

        return message;
    }

    // 6자리만 인증번호로 채택
    private String createRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(UUID_PREFIX_START, UUID_PREFIX_END);
    }
}
