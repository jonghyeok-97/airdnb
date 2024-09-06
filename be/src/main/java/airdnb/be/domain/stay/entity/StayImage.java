package airdnb.be.domain.stay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StayImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stayImageId;

    private Long stayId;

    private String url;

    private StayImage(Long stayId, String url) {
        this.stayId = stayId;
        this.url = url;
    }

    public static List<StayImage> from(Long stayId, List<String> urls) {
        return urls.stream()
                .map(url -> new StayImage(stayId, url))
                .collect(Collectors.toList());
    }
}
