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
        // 인증코드가 중복될 경우가 없기 때문에 key 로 설정
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
}
