package airdnb.be.member.service;

import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.utils.RedisUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
    private static final Long UUID_TTL = 5L;
    private static final TimeUnit UUID_TTL_UNIT = TimeUnit.MINUTES;

    private final Environment env;
    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;

    public void sendAuthenticationEmail(String memberEmail) {
        try {
            String randomUUID = createRandomUUID();
            SimpleMailMessage message = createSimpleMessage(memberEmail, randomUUID);
            javaMailSender.send(message);
            redisUtils.addData(randomUUID, memberEmail, UUID_TTL, UUID_TTL_UNIT);
        } catch (MailException ex) {
            log.error("메일 전송 불가 : {}", ex.getMessage());
        }
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
        throw new BusinessException(ErrorCode.AUTH_CODE_MISMATCH);
    }
}
