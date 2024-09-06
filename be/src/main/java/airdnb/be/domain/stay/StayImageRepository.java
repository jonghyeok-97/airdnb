package airdnb.be.domain.stay;

import airdnb.be.domain.stay.entity.StayImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StayImageRepository extends JpaRepository<StayImage, Long> {

    List<StayImage> findStayImagesByStayId(Long stayId);
}
