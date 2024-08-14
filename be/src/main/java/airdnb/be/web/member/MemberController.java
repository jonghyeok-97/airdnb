package airdnb.be.web.member;

import airdnb.be.domain.member.service.EmailService;
import airdnb.be.domain.member.service.MemberService;
import airdnb.be.web.member.request.EmailAuthenticationRequest;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.be.web.member.request.MemberLoginRequest;
import airdnb.be.web.member.request.MemberSaveRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final EmailService emailService;

    /**
     * @return HttpStatus.OK(200) 는 로그인 창 HttpStatus.NO_CONTENT(204) 는 회원가입
     */
    @GetMapping("/exist")
    public ResponseEntity<Void> existsMemberByEmail(@RequestBody @Valid EmailRequest request) {
        boolean existsMember = memberService.existsMemberByEmail(request.email());
        if (existsMember) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
    이메일 인증번호 확인
     */
    @GetMapping("/email/authenticate")
    public void authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request, HttpSession httpSession) {
        emailService.authenticateEmail(request.authCode(), request.email());
        httpSession.setAttribute(VERIFIED_MEMBER, true);
    }

    /*
    이메일 인증번호 보내기
     */
    @PostMapping("/email/authenticate")
    public void sendAuthenticationEmail(@RequestBody @Valid EmailRequest request) {
        emailService.sendAuthenticationEmail(request.email());
    }

    @PostMapping
    public void addMember(@RequestBody @Valid MemberSaveRequest request, HttpSession httpSession) {
        Object sessionAttribute = httpSession.getAttribute(VERIFIED_MEMBER);
        if (sessionAttribute == null) {
            throw new HttpClientErrorException("인증되지 않은 회원입니다", HttpStatus.BAD_REQUEST, "BAD_REQUEST", null, null, null);
        }
        httpSession.invalidate();
        memberService.addMember(request.toMember());
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid MemberLoginRequest request, HttpSession httpSession) {
        boolean login = memberService.login(request.email(), request.password());
        if (login) {
            httpSession.setAttribute(LOGIN_MEMBER, true);
        }
        throw new HttpClientErrorException("아이디와 비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST, "BAD_REQUEST", null, null, null);
    }
}
