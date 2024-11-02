package airdnb.be.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

    /**
     * @apiNote  메일 예외 발생 시, 1초마다 총 3회 재시도. 재시도 다 실패하면 로그 레벨 error 발생
     */
    @Async("mailExecutor")
    @Retryable(retryFor = MailSendException.class, backoff = @Backoff(delay = 1000))
    public void sendAsyncAuthenticationMail(String toEmail, String authenticationCode) {
        SimpleMailMessage mail = createMailMessage(toEmail, authenticationCode);
        javaMailSender.send(mail);
        log.info("[메일] 메일 전송={}", toEmail);
    }

    @Recover
    private void occurMailServerError() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.error("[메일] 메일 서버에 메일을 보내는 데 실패했습니다. 메서드명={}", methodName);
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
