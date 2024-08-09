package airdnb.be.interceptor;

import static airdnb.be.web.member.MemberController.LOGIN_MEMBER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object loginMember = session.getAttribute(LOGIN_MEMBER);
        return loginMember != null;
    }
}
