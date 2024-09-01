package airdnb.be.domain.mail;

import airdnb.be.client.MailClient;
import airdnb.be.client.RedisClient;
import airdnb.be.domain.mail.request.EmailAuthenticationServiceRequest;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private static final Long UUID_TTL = 5L;
    private static final TimeUnit UUID_TTL_UNIT = TimeUnit.MINUTES;

    private final MailClient mailClient;
    private final RedisClient redisClient;

    /**
     * @apiNote  메일 예외 발생 시, 1초마다 총 3회 재시도. 재시도 다 실패하면 로그 레벨 error 발생
     */
    @Retryable(retryFor = MailSendException.class, backoff = @Backoff(delay = 1000))
    public void sendAuthenticationMail(EmailAuthenticationServiceRequest request) {
        String email = request.email();
        String authenticationCode = request.authenticationCode();

        mailClient.sendAuthenticationMail(email, authenticationCode);
        redisClient.addData(authenticationCode, email, UUID_TTL, UUID_TTL_UNIT);
    }

    @Recover
    private void occurMailServerError() {
        log.error("메일 서버에 메일을 보낼 수 없습니다");
        throw new BusinessException(ErrorCode.MAIL_SERVER_ERROR);
    }

    /**
     * @apiNote 사용자가 인증코드 확인을 요청했을 때
     */
    public boolean isValidMail(EmailAuthenticationServiceRequest request) {
        return redisClient.hasData(request.authenticationCode(), request.email());
    }

    /**
     * @apiNote 사용자가 메일을 인증했을 때, 검증된 이메일을 세션 추가
     */
    public void setVerifiedMail(String mail, String sessionAttribute) {
        if (sessionAttribute == null) {
            throw new BusinessException(ErrorCode.AUTH_MISMATCH);
        }
        redisClient.addData(mail, sessionAttribute);
    }

    /**
     * @apiNote 회원가입 할 때, 검증된 이메일로 회원가입하는지 확인
     */
    public void checkVerifiedMail(String mail, String sessionAttribute) {
        if (sessionAttribute != null && redisClient.hasData(mail, sessionAttribute)) {
            redisClient.deleteData(mail);
            return;
        }
        throw new BusinessException(ErrorCode.AUTH_MISMATCH);
    }
}
