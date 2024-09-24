package airdnb.docs.member;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static airdnb.be.utils.SessionConst.MAIL_VERIFIED_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import airdnb.be.domain.mail.MailService;
import airdnb.be.domain.member.service.MemberService;
import airdnb.be.web.member.MemberController;
import airdnb.be.web.member.request.EmailAuthenticationRequest;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.be.web.member.request.MemberLoginRequest;
import airdnb.be.web.member.request.MemberSaveRequest;
import airdnb.docs.RestDocsSupport;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

public class MemberControllerDocs extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);
    private final MailService mailService = mock(MailService.class);

    @Override
    protected Object initController() {
        return new MemberController(memberService, mailService);
    }

    @DisplayName("회원이 가입되어 있으면 200 반환하는 API")
    @Test
    void existsMemberByEmail() throws Exception {
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
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("/member/member-exist-200",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @DisplayName("회원이 가입되지 않았으면 204 반환하는 API")
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
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("/member/member-exist-204",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @DisplayName("이메일 인증 성공 API")
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
                .andExpect(request().sessionAttribute(MAIL_VERIFIED_MEMBER, true))
                .andDo(document("/member/member-email-authenticate-200",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 메일"),
                                fieldWithPath("authCode").type(JsonFieldType.STRING).description("메일 인증 코드")
                        )));
    }

    @DisplayName("이메일 인증 실패 API")
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
                .andExpect(request().sessionAttributeDoesNotExist(MAIL_VERIFIED_MEMBER))
                .andDo(document("/member/member-email-authenticate-400",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @DisplayName("이메일 인증번호 보내는 API")
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
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("/member/member-email-authenticate-send",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 메일")
                        )));

        verify(mailService, times(1)).sendAuthenticationMail(any());
    }

    @DisplayName("회원가입 API")
    @Test
    void addMemberWithoutEmailAuthenticate() throws Exception {
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
                .andDo(document("/member/member-register",
                        preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("회원 전화번호"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                        )))
                .andReturn();

        HttpSession session = mvcResult.getRequest().getSession(false);
        assertThat(session).isNull();
    }

    @DisplayName("로그인 API")
    @Test
    void login() throws Exception {
        // given
        MemberLoginRequest request = new MemberLoginRequest(
                "123@naver.com", "비밀번호");

        given(memberService.login(request.toServiceRequest()))
                .willReturn(1L);

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
                .andExpect(request().sessionAttribute(LOGIN_MEMBER, 1L))
                .andDo(document("/member/member-login",
                        preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                        )));
    }
}
