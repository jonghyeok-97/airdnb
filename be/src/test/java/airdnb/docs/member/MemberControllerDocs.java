package airdnb.docs.member;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import airdnb.be.domain.mail.MailService;
import airdnb.be.domain.member.service.MemberService;
import airdnb.be.web.member.MemberController;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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
                .andDo(document("member-exist-200",
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
                .andDo(document("member-exist-204",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));;
    }
}
