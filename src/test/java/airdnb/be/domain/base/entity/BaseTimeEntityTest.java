package airdnb.be.domain.base.entity;

import static org.assertj.core.api.Assertions.assertThat;

import airdnb.be.IntegrationTestSupport;
import airdnb.be.domain.member.MemberRepository;
import airdnb.be.domain.member.entitiy.Member;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BaseTimeEntityTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("베이스 엔티티를 상속하면 생성된 날짜시간과 마지막으로 업데이트된 날짜시간이 생성된다")
    @Test
    void createdAndUpdateDateTime() {
        // given
        Member member = new Member("이름", "1@naver.com", "010-1234-1234", "password1");
        Member saved = memberRepository.save(member);

        // when
        LocalDateTime createdDateTime = saved.getCreatedDateTime();
        LocalDateTime modifiedDateTime = saved.getModifiedDateTime();

        // then
        assertThat(createdDateTime).isInThePast();
        assertThat(modifiedDateTime).isInThePast();
    }
}