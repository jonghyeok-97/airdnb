package airdnb.be.web.member;

import airdnb.be.web.ApiResponse;
import airdnb.be.web.member.request.EmailAuthenticationRequest;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.be.domain.mail.MailService;
import airdnb.be.domain.member.service.MemberService;
import airdnb.be.web.member.request.MemberLoginRequest;
import airdnb.be.web.member.request.MemberSaveRequest;
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

    private static final String VERIFIED_MEMBER = "verified_member";
    public static final String LOGIN_MEMBER = "login_member";

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
    public ApiResponse<Void> authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request, HttpSession session) {
        mailService.authenticateEmail(request.authCode(), request.email()); // 인증번호 확인
        session.setAttribute(VERIFIED_MEMBER, true); // 세션 설정
        mailService.setVerifiedEmail(request.email(), session.getAttribute(VERIFIED_MEMBER)); // 검증된 이메일 추가

        return ApiResponse.ok();
    }

    /*
    이메일 인증번호 보내기
     */
    @PostMapping("/email/authenticate")
    public ApiResponse<Void> sendAuthenticationEmail(@RequestBody @Valid EmailRequest request) {
        mailService.sendAuthenticationEmail(request.email());

        return ApiResponse.ok();
    }

    @PostMapping
    public ApiResponse<Long> addMember(@RequestBody @Valid MemberSaveRequest request, HttpSession session) {
        mailService.verifiedEmail(request.email(), session.getAttribute(VERIFIED_MEMBER)); // 검증된 이메일로 요청이 왔는지 확인
        session.invalidate();

        return ApiResponse.ok(memberService.addMember(request.toServiceRequest()));
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody @Valid MemberLoginRequest request, HttpSession httpSession) {
        memberService.login(request.toServiceRequest());
        httpSession.setAttribute(LOGIN_MEMBER, true);

        return ApiResponse.ok();
    }
}
