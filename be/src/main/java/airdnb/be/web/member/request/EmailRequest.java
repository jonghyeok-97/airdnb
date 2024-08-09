package airdnb.be.web.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(

        /**
         * 모든 이메일에 인증 메일을 전송하지 않게 하여 서버의 부하를 낮추고자 함.
         * Gmail 경우, 로컬에 + 에 포함 가능 -> 도메인 부분만 엄격하게 검증
         *
         * 로컬 : @ 문자를 제외한 모든 문자가 하나 이상 포함
         * @ : 전체 문자에서 단 하나
         * 도메인
         *      [^-] : @ 뒤에 - 로 시작하지 않음
         *      [A-Za-z0-9-]+ :  알파벳, 숫자가 1개 이상 포함되어야 함
         *      (\.[A-Za-z0-9-]+)* : 위의 표현식이 . 을 포함해서 0번이상 반복될 수 있음
         *      (\.[A-Za-z]{2,})$ : 끝은 알파벳 2자리 이상으로 끝나야 함
         */
        @Email(regexp = "^[^@]+@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
        @NotNull
        String email
) {}
