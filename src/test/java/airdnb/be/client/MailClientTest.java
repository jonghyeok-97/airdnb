package airdnb.be.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

import airdnb.be.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class MailClientTest extends IntegrationTestSupport {

    @Autowired
    private MailClient mailClient;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("해당 이메일로 인증번호를 1번 보낸다")
    @Test
    void sendAuthenticationMail() {
        // given when
        mailClient.sendAsyncAuthenticationMail("test@naver.com", "13mxken34");

        // then
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}