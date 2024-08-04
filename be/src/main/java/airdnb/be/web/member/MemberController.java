package airdnb.be.web.member;

import airdnb.be.web.member.request.EmailAuthenticationRequest;
import airdnb.be.web.member.request.EmailRequest;
import airdnb.be.web.member.request.MemberSaveRequest;
import airdnb.be.domain.member.service.EmailService;
import airdnb.be.domain.member.service.MemberService;
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

    @GetMapping("/email")
    public void authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request) {
        emailService.authenticateEmail(request.authCode(), request.email());
    }

    @PostMapping("/email")
    public void sendAuthenticationEmail(@RequestBody @Valid EmailRequest request) {
        emailService.sendAuthenticationEmail(request.email());
    }

    @PostMapping
    public void addMember(@RequestBody @Valid MemberSaveRequest request) {
        memberService.addMember(request.toMember());
    }
}
