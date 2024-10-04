package airdnb.be.domain.review.entity;

import airdnb.be.domain.base.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    @Column(nullable = false, name = "writer_id")
    private Long memberId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Double starRating;

    public Review(Long memberId, String content, Double starRating) {
        this.memberId = memberId;
        this.content = content;
        this.starRating = starRating;
    }
}
