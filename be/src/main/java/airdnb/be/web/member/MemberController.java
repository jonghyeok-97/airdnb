package airdnb.be.web.member;

import static airdnb.be.utils.SessionConst.LOGIN_MEMBER;
import static airdnb.be.utils.SessionConst.MAIL_VERIFIED_MEMBER;

import airdnb.be.domain.mail.MailService;
import airdnb.be.domain.mail.request.EmailAuthenticationServiceRequest;
import airdnb.be.domain.member.service.MemberService;
import airdnb.be.utils.RandomUUID;
import airdnb.be.web.ApiResponse;
import airdnb.be.web.member.request.EmailAuthenticationRequest;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.be.web.member.request.MemberLoginRequest;
import airdnb.be.web.member.request.MemberSaveRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;

    /**
     * @return HttpStatus.OK(200) 는 로그인 창 HttpStatus.NO_CONTENT(204) 는 회원가입
     */
    @GetMapping("/exist")
    public ApiResponse<Void> existsMemberByEmail(@RequestBody @Valid EmailRequest request) {
        boolean existsMember = memberService.existsMemberByEmail(request.email());
        if (existsMember) {
            return ApiResponse.ok();
        }
        return ApiResponse.status(HttpStatus.NO_CONTENT);
    }

    /*
    이메일 인증번호 확인
     */
    @GetMapping("/email/authenticate")
    public ApiResponse<Void> authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request,
                                               HttpServletRequest servletRequest) {
        if (mailService.isValidMail(request.toServiceRequest())) {  // 메일 인증 번호 확인
            HttpSession session = servletRequest.getSession(); // 세션이 있으면 세션 반환, 없으면 새로운 세션 생성
            session.setAttribute(MAIL_VERIFIED_MEMBER, true);
            return ApiResponse.ok();
        }
        return ApiResponse.status(HttpStatus.BAD_REQUEST);
    }

    /*
    이메일 인증번호 보내기
     */
    @PostMapping("/email/authenticate")
    public ApiResponse<Void> sendAuthenticationEmail(@RequestBody @Valid EmailRequest request) {
        String uuid = RandomUUID.createSixDigitUUID();
        mailService.sendAuthenticationMail(new EmailAuthenticationServiceRequest(request.email(), uuid));

        return ApiResponse.ok();
    }

    @PostMapping
    public ApiResponse<Long> addMember(@RequestBody @Valid MemberSaveRequest request,
                                       HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        if (session == null || !((boolean) session.getAttribute(MAIL_VERIFIED_MEMBER))) {
            return ApiResponse.status(HttpStatus.BAD_REQUEST);
        }
        memberService.addMember(request.toServiceRequest());
        session.invalidate();
        return ApiResponse.ok();
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody @Valid MemberLoginRequest request,
                                   HttpServletRequest servletRequest) {
        memberService.login(request.toServiceRequest());
        servletRequest.getSession().setAttribute(LOGIN_MEMBER, true);

        return ApiResponse.ok();
    }
}
