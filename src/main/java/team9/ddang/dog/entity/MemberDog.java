package team9.ddang.dog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.global.entity.BaseEntity;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberDogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id",nullable = false)
    private Dog dog;

    //소프트 삭제
    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted = IsDeleted.FALSE;

    @Builder
    private MemberDog(Member member, Dog dog) {
        this.member = member;
        this.dog = dog;
    }

    //소프트 삭제
    public void setIsDeleted(IsDeleted isDeleted) {
        this.isDeleted = IsDeleted.TRUE;
    }
}
