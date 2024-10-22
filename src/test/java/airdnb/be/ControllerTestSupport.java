package airdnb.be;

import airdnb.be.domain.mail.MailService;
import airdnb.be.domain.member.service.MemberService;
import airdnb.be.domain.payment.service.PaymentFacade;
import airdnb.be.domain.payment.service.PaymentService;
import airdnb.be.domain.reservation.service.ReservationService;
import airdnb.be.domain.review.ReviewService;
import airdnb.be.domain.stay.service.StayService;
import airdnb.be.web.member.MemberController;
import airdnb.be.web.payment.PaymentController;
import airdnb.be.web.reservation.ReservationController;
import airdnb.be.web.review.ReviewController;
import airdnb.be.web.stay.StayController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        MemberController.class,
        StayController.class,
        ReservationController.class,
        ReviewController.class,
        PaymentController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MailService mailService;

    @MockBean
    protected StayService stayService;

    @MockBean
    protected ReservationService reservationService;

    @MockBean
    protected ReviewService reviewService;

    @MockBean
    protected PaymentService paymentService;

    @MockBean
    protected PaymentFacade paymentFacade;
}
