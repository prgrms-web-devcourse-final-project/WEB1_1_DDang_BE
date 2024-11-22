package team9.ddang.family.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.global.entity.BaseEntity;
import team9.ddang.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long familyId;

    @Column(nullable = false, length = 20)
    private String familyName;

    @Column(nullable = false, length = 50)
    private String familyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Family(String familyName, String familyCode, Member member) {
        this.familyName = familyName;
        this.familyCode = familyCode;
        this.member = member;
    }
}
// TODO : 나중에 더 추가할 예정