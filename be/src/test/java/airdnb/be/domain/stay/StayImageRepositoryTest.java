package airdnb.be.domain.stay;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import airdnb.be.domain.stay.entity.Stay;
import airdnb.be.domain.stay.entity.StayImage;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StayImageRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private StayImageRepository stayImageRepository;

    @DisplayName("숙소Id에 해당하는 모든 숙소이미지를 찾는다.")
    @Test
    void test() {
        // given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Stay stay = createStay(savedMember);
        Stay savedStay = stayRepository.save(stay);

        List<String> urls = List.of("url1", "url2", "url3", "url4", "url5");
        List<StayImage> stayImages = StayImage.from(savedStay.getStayId(), urls);
        stayImageRepository.saveAll(stayImages);

        // when
        List<StayImage> found = stayImageRepository.findStayImagesByStayId(savedStay.getStayId());

        // then
        assertThat(found).hasSize(5);
    }

    private Member createMember() {
        return new Member("이름", "123@naver.com", "전화번호", "패스워드");
    }

    private Stay createStay(Member member) {
        return new Stay(
                member.getId(),
                "제목",
                "설명",
                LocalTime.of(15, 0),
                LocalTime.of(11, 0),
                new BigDecimal(50000),
                1,
                108d,
                54d);
    }
}