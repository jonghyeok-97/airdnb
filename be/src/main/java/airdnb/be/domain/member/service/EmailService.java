package airdnb.be.domain.member.service;

import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.utils.RedisUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private static final String MAIE_SENDER = "spring.mail.username";
    private static final String TITLE = "Airdnb 클론 프로젝트에서 보낸 인증번호 입니다";
    private static final int UUID_PREFIX_START = 0;
    private static final int UUID_PREFIX_END = 6;
    private static final Long UUID_TTL = 5L;
    private static final TimeUnit UUID_TTL_UNIT = TimeUnit.MINUTES;

    private final Environment env;
    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;

    @Retryable(retryFor = MailSendException.class, backoff = @Backoff(delay = 1000))
    public void sendAuthenticationEmail(String memberEmail) {
        String randomUUID = createRandomUUID();
        SimpleMailMessage message = createSimpleMessage(memberEmail, randomUUID);
        javaMailSender.send(message);
        redisUtils.addData(randomUUID, memberEmail, UUID_TTL, UUID_TTL_UNIT);
    }

    @Recover
    private void occurMailServerError() {
        log.error("메일 서버에 메일을 보낼 수 없습니다");
        throw new BusinessException(ErrorCode.MAIL_SERVER_ERROR);
    }

    private SimpleMailMessage createSimpleMessage(String memberEmail, String randomUUID) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(env.getProperty(MAIE_SENDER));
        message.setTo(memberEmail);
        message.setSubject(TITLE);
        message.setText(randomUUID);

        return message;
    }

    // 6자리만 인증번호로 채택
    private String createRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(UUID_PREFIX_START, UUID_PREFIX_END);
    }

    public void authenticateEmail(String authCode, String email) {
        if (redisUtils.hasData(authCode, email)) {
            return;
        }
        throw new BusinessException(ErrorCode.AUTH_MISMATCH);
    }

    public void setVerifiedEmail(String email, Object sessionAttribute) {
        if (sessionAttribute == null) {
            throw new BusinessException(ErrorCode.AUTH_MISMATCH);
        }
        redisUtils.addData(email, (String) sessionAttribute);
    }

    public void verifiedEmail(String email, Object sessionAttribute) {
        if (sessionAttribute != null && redisUtils.hasData(email, (String) sessionAttribute)) {
            redisUtils.deleteData(email);
            return;
        }
        throw new BusinessException(ErrorCode.AUTH_MISMATCH);
    }
}
