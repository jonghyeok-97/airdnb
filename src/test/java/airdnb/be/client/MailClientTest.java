package airdnb.be.client;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import airdnb.be.IntegrationTestSupport;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class MailClientTest extends IntegrationTestSupport {

    @Autowired
    private MailClient mailClient;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("비동기로 인증 이메일 보낼 때 실패 시나리오")
    @TestFactory
    Collection<DynamicTest> sendAsyncAuthenticationMail() {
        // given
        String email = "test@naver.com";
        String authenticationCode = "authCode";

        willThrow(MailSendException.class)
                .given(javaMailSender)
                .send(any(SimpleMailMessage.class));

        // when then
        return List.of(
                dynamicTest("이메일 보낼 때 예외가 발생하면 AsyncUncaughtExceptionHandler 에 의해 예외가 잡힌다.", () ->
                        assertThatCode(() -> mailClient.sendAsyncAuthenticationMail(email, authenticationCode))
                                .doesNotThrowAnyException())
                ,
                dynamicTest("이메일 보내기에 실패하면 총 3번의 시도를 한다", () -> {
                    Thread.sleep(5000);
                    verify(javaMailSender, times(3)).send(any(SimpleMailMessage.class));
                })
        );
    }
}