package airdnb.be.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailClient {

    private static final String MAIL_SENDER = "spring.mail.username";
    private static final String TITLE = "Airdnb 클론 프로젝트에서 보낸 인증번호 입니다";
    private static final int RETRY_MAX_ATTEMPTS = 3;

    private final Environment env;
    private final JavaMailSender javaMailSender;

    /**
     * @apiNote 메일 예외 발생 시, 1초마다 총 3회 재시도
     */
    @Async("mailExecutor")
    @Retryable(retryFor = {MailSendException.class}, backoff = @Backoff(delay = 1000))
    public void sendAsyncAuthenticationMail(String toEmail, String authenticationCode) {
        int retryCount = RetrySynchronizationManager.getContext().getRetryCount();
        log.info("[메일 전송 시도] toEmail={}, 시도 횟수={}/{}", toEmail, ++retryCount, RETRY_MAX_ATTEMPTS);

        SimpleMailMessage mail = createMailMessage(toEmail, authenticationCode);
        javaMailSender.send(mail);
        log.info("[메일 전송 완료] toEmail={}", toEmail);
    }

    @Recover
    public void recoverSendAsyncAuthenticationMail(MailException ex) {
        log.error("[메일 전송 실패] {}", ex.getMessage(), ex);
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
