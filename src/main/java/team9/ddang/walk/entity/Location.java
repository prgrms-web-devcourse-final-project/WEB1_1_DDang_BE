package team9.ddang.walk.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void updateWalk(Walk walk){
        this.walk = walk;
    }
}
