package team9.ddang.walk.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Embedded
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id",nullable = false)
    private Walk walk;

    @Builder
    private Location(Position position, Walk walk) {
        this.position = position;
        this.walk = walk;
    }

    public static Location createTemp(double longitude, double latitude, LocalDateTime timeStamp){
        return Location.builder()
                .position(Position.builder()
                        .longitude(longitude)
                        .latitude(latitude)
                        .timeStamp(timeStamp)
                        .build())
                .build();
    }

    public void updateWalk(Walk walk){
        this.walk = walk;
    }
}
