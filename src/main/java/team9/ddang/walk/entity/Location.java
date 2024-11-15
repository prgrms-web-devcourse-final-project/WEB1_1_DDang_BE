package team9.ddang.walk.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id",nullable = false)
    private Walk walk;

    @Builder
    private Location(BigDecimal latitude, BigDecimal longitude, LocalDateTime timeStamp, Walk walk) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.walk = walk;
    }
}
