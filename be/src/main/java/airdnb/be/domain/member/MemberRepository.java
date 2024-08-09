package airdnb.be.domain.member;

import airdnb.be.domain.member.entitiy.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Member findMemberByEmail(String email);
}
