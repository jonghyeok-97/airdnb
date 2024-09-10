package airdnb.be.domain.reservation;

import airdnb.be.domain.reservation.entity.ReservationDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationDateRepository extends JpaRepository<ReservationDate, Long> {

    List<ReservationDate> findReservationDatesByStayId(Long stayId);
}
