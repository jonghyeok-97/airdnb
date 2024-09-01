package airdnb.be.domain.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willThrow;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.client.MailClient;
import airdnb.be.client.RedisClient;
import airdnb.be.domain.mail.request.EmailAuthenticationServiceRequest;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;

class MailServiceTest extends IntegrationTestSupport {

    @Autowired
    private MailService mailService;

    @MockBean
    private MailClient mailClient;

    @Autowired
    private RedisClient redisClient;

    @DisplayName("회원가입 시 이메일 검증 시나리오")
    @TestFactory
    Collection<DynamicTest> sendAuthenticationMailSuccess() {
        // given
        String email = "gromit@naver.com";
        String authenticationCode = "3gkdn4";
        EmailAuthenticationServiceRequest request = new EmailAuthenticationServiceRequest(email, authenticationCode);

        // when
        mailService.sendAuthenticationMail(request);

        return List.of(
                dynamicTest("타겟 메일에 인증 코드를 보낸다", () -> {
                    // then
                    verify(mailClient, times(1))
                            .sendAuthenticationMail(eq(request.email()), eq(request.authenticationCode()));
                }),

                dynamicTest("타겟 메일과 인증 코드가 저장된다", () -> {
                    // then
                    boolean result = redisClient.hasData(request.authenticationCode(), request.email());
                    assertThat(result).isTrue();
                }),

                dynamicTest("사용자는 받은 메일 인증 코드로 이메일 검증 요청을 한다.", () -> {
                    // when
                    boolean result = mailService.isValidMail(request);

                    // then
                    assertThat(result).isTrue();
                }),

                dynamicTest("이메일 검증에 성공하면 검증된 이메일을 임시 저장한다", () -> {
                    // given
                    String sessionAttribute = "31289y1njda";

                    // when
                    mailService.setVerifiedMail(email, sessionAttribute);

                    // then
                    boolean result = redisClient.hasData(email, sessionAttribute);
                    assertThat(result).isTrue();
                }),

                dynamicTest("검증된 이메일로 회원가입을 진행하면 임시 저장한 이메일을 삭제한다.", () -> {
                    // given
                    String sessionAttribute = "31289y1njda";

                    // when
                    mailService.checkVerifiedMail(email, sessionAttribute);

                    // then
                    boolean result = redisClient.hasData(email, sessionAttribute);
                    assertThat(result).isFalse();
                })
        );
    }

    @DisplayName("타겟 메일에 인증 코드 보낼 때 실패 시나리오")
    @TestFactory
    Collection<DynamicTest> sendAuthenticationMailFail() {
        // given
        String email = "gromit@naver.com";
        String authenticationCode = "3gkdn4";
        EmailAuthenticationServiceRequest request = new EmailAuthenticationServiceRequest(email, authenticationCode);

        willThrow(MailSendException.class)
                .given(mailClient)
                .sendAuthenticationMail(anyString(), anyString());

        return List.of(
                dynamicTest("메일 보내기에 실패하면 예외가 발생한다", () -> {
                    // when then
                    assertThatThrownBy(() -> mailService.sendAuthenticationMail(request))
                            .isInstanceOf(BusinessException.class)
                            .extracting("errorCode")
                            .isEqualTo(ErrorCode.MAIL_SERVER_ERROR);
                }),
                dynamicTest("메일 보내기 재시도는 총 3번 했다", () -> {
                    // then
                    verify(mailClient, times(3)).sendAuthenticationMail(anyString(), anyString());
                }),

                dynamicTest("사용자는 메일 인증에 실패한다", () -> {
                    // when
                    boolean result = mailService.isValidMail(request);

                    // then
                    assertThat(result).isFalse();
                }),

                dynamicTest("사용자가 메일 인증에 실패하면 메일을 임시 저장하지 않고 예외가 발생한다", () -> {
                    // given
                    String sessionAttribute = null;

                    // when then
                    assertThatThrownBy(() -> mailService.setVerifiedMail(email, sessionAttribute))
                            .isInstanceOf(BusinessException.class)
                            .extracting("errorCode")
                            .isEqualTo(ErrorCode.AUTH_MISMATCH);
                }),

                dynamicTest("검증된 이메일로 회원가입 하지 않으면 예외가 발생한다", () -> {
                    // given
                    String sessionAttribute = null;

                    // when then
                    assertThatThrownBy(() -> mailService.checkVerifiedMail(email, sessionAttribute))
                            .isInstanceOf(BusinessException.class)
                            .extracting("errorCode")
                            .isEqualTo(ErrorCode.AUTH_MISMATCH);
                }),

                dynamicTest("조작된 세션값으로 회원가입을 진행하면 예외가 발생한다", () -> {
                    // given
                    String sessionAttribute = "changedSessionValue";

                    // when then
                    assertThatThrownBy(() -> mailService.checkVerifiedMail(email, sessionAttribute))
                            .isInstanceOf(BusinessException.class)
                            .extracting("errorCode")
                            .isEqualTo(ErrorCode.AUTH_MISMATCH);
                })
        );
    }
}