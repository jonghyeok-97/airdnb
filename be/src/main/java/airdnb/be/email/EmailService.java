package airdnb.be.email;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final Environment env;
    private final JavaMailSender javaMailSender = new JavaMailSenderImpl();

    public void sendVerificationEmail(String memberEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(env.getProperty("spring.mail.username"));
            message.setTo(memberEmail);
            message.setSubject("Airdnb 클론 프로젝트에서 보낸 인증번호 입니다");
            message.setText(createRandomUUID());

            javaMailSender.send(message);
        } catch (MailException e) {
            // TODO: 메일 에러 핸들링하기
            log.error("메일 보낼 때 에러 발생 : {}", e.getMessage());
        }
    }

    // 6자리만 인증번호로 채택
    private String createRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 6);
    }
}
