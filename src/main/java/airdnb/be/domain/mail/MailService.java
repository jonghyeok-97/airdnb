package airdnb.be.domain.mail;

import airdnb.be.client.MailClient;
import airdnb.be.client.RedisClient;
import airdnb.be.domain.mail.request.EmailAuthenticationServiceRequest;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private static final Long UUID_TTL = 5L;
    private static final TimeUnit UUID_TTL_UNIT = TimeUnit.MINUTES;

    private final MailClient mailClient;
    private final RedisClient redisClient;

    public void sendAuthenticationMail(EmailAuthenticationServiceRequest request) {
        String email = request.email();
        String authenticationCode = request.authenticationCode();
        mailClient.sendAsyncAuthenticationMail(email, authenticationCode);
        // 인증코드가 중복될 경우가 없기 때문에 key 로 설정
        redisClient.addData(authenticationCode, email, UUID_TTL, UUID_TTL_UNIT);
    }

    /**
     * @apiNote 사용자가 인증코드 확인을 요청했을 때
     */
    public boolean isValidMail(EmailAuthenticationServiceRequest request) {
        return redisClient.hasData(request.authenticationCode(), request.email());
    }
}
