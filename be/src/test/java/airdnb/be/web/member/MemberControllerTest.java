package airdnb.be.web.member;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static airdnb.be.utils.SessionConst.MAIL_VERIFIED_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import airdnb.be.ControllerTestSupport;
import airdnb.be.exception.BusinessException;
import airdnb.be.exception.ErrorCode;
import airdnb.be.web.member.request.EmailAuthenticationRequest;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.be.web.member.request.MemberLoginRequest;
import airdnb.be.web.member.request.MemberSaveRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class MemberControllerTest extends ControllerTestSupport {

    @DisplayName("회원이 존재하면 OK_200 을 반환한다.")
    @Test
    void existsMemberByEmail() throws Exception {
        // given
        EmailRequest request = new EmailRequest("123@naver.com");
        given(memberService.existsMemberByEmail(request.email()))
                .willReturn(true);

        // when then
        mockMvc.perform(
                        get("/member/exist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원이 존재하지 않으면 NO_CONTENT_204 반환한다.")
    @Test
    void notExistsMemberByEmail() throws Exception {
        // given
        EmailRequest request = new EmailRequest("123@naver.com");
        given(memberService.existsMemberByEmail(request.email()))
                .willReturn(false);

        // when then
        mockMvc.perform(
                        get("/member/exist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0204"))
                .andExpect(jsonPath("$.status").value("NO_CONTENT"))
                .andExpect(jsonPath("$.message").value("NO_CONTENT"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("이메일 인증이 성공하면 OK_200 과 세션을 반환한다.")
    @Test
    void authenticateEmail() throws Exception {
        // given
        String email = "123@naver.com";
        String authenticationCode = "312nj5acz";
        EmailAuthenticationRequest request = new EmailAuthenticationRequest(email, authenticationCode);

        given(mailService.isValidMail(request.toServiceRequest()))
                .willReturn(true);

        // when then
        mockMvc.perform(
                        get("/member/email/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(request().sessionAttribute(MAIL_VERIFIED_MEMBER, true));
    }

    @DisplayName("이메일 인증이 실패하면 BAD_REQUEST_400을 반환하고, 세션은 없다.")
    @Test
    void authenticateEmailFail() throws Exception {
        // given
        String email = "123@naver.com";
        String authenticationCode = "312njacz";
        EmailAuthenticationRequest request = new EmailAuthenticationRequest(email, authenticationCode);

        given(mailService.isValidMail(request.toServiceRequest()))
                .willReturn(false);

        // when then
        mockMvc.perform(
                        get("/member/email/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(request().sessionAttributeDoesNotExist(MAIL_VERIFIED_MEMBER));
    }

    @DisplayName("이메일 인증에 인증번호를 보내면 OK_200 이다.")
    @Test
    void sendAuthenticationEmail() throws Exception {
        // given
        EmailRequest request = new EmailRequest("123@naver.com");

        // when then
        mockMvc.perform(
                        post("/member/email/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(mailService, times(1)).sendAuthenticationMail(any());
    }

    @DisplayName("회원가입 할 때, 이메일 검증이 되지 않으면 BAD_REQUEST 400 이다.")
    @Test
    void addMemberWithoutEmailAuthenticate() throws Exception {
        // given
        MemberSaveRequest request = new MemberSaveRequest(
                "이름", "123@naver.com", "010-1234-5678", "비밀번호");

        // when then
        mockMvc.perform(
                        post("/member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(request().sessionAttributeDoesNotExist(MAIL_VERIFIED_MEMBER));

        verify(memberService, times(0)).addMember(request.toServiceRequest());
    }

    @DisplayName("회원가입 할 때, 이메일 검증을 했으면 OK_200 이며 세션이 삭제된다.")
    @Test
    void addMemberWithEmailAuthenticate() throws Exception {
        // given
        MemberSaveRequest request = new MemberSaveRequest(
                "이름", "123@naver.com", "010-1234-5678", "비밀번호");

        given(memberService.addMember(request.toServiceRequest()))
                .willReturn(anyLong());

        // when then
        MvcResult mvcResult = mockMvc.perform(
                        post("/member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .sessionAttr(MAIL_VERIFIED_MEMBER, true))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull();
    }

    @DisplayName("로그인하면 세션이 부여되며 OK_200 이다.")
    @Test
    void login() throws Exception {
        // given
        MemberLoginRequest request = new MemberLoginRequest(
                "123@naver.com", "비밀번호");

        willDoNothing()
                .given(memberService)
                .login(request.toServiceRequest());

        // when then
        mockMvc.perform(
                        post("/member/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("0200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(request().sessionAttribute(LOGIN_MEMBER, true));
    }

    @DisplayName("로그인에 실패하면 세션이 없고, ErrorCode 를 반환한다.")
    @Test
    void loginFail() throws Exception {
        // given
        MemberLoginRequest request = new MemberLoginRequest(
                "123@naver.com", "비밀번호");

        willThrow(new BusinessException(ErrorCode.NOT_EXIST_MEMBER))
                .given(memberService)
                .login(request.toServiceRequest());

        // when then
        mockMvc.perform(
                        post("/member/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("1002"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(request().sessionAttributeDoesNotExist(LOGIN_MEMBER));
    }
}