package team9.ddang.family.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.dog.entity.Dog;
import team9.ddang.global.entity.BaseEntity;
import team9.ddang.member.entity.Member;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkScheduleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime walkTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id",nullable = false)
    private Dog dog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id",nullable = false)
    private Family family;

    @Builder
    private WalkSchedule(Member member, Dog dog, DayOfWeek dayOfWeek, LocalTime walkTime, Family family) {
        this.member = member;
        this.dog = dog;
        this.dayOfWeek = dayOfWeek;
        this.walkTime = walkTime;
        this.family = family;
    }
}
