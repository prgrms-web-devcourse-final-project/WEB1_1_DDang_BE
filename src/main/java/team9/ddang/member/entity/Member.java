package team9.ddang.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.family.entity.Family;
import team9.ddang.global.entity.BaseEntity;
import team9.ddang.global.entity.Gender;
import team9.ddang.global.entity.IsDeleted;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole familyRole;

    @Column(nullable = false)
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsMatched isMatched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    private Member(Long memberId, String name, String email, Gender gender, String address, FamilyRole familyRole, Provider provider, String profileImg, IsMatched isMatched, Family family, Role role) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.familyRole = familyRole;
        this.provider = provider;
        this.profileImg = profileImg;
        this.isMatched = isMatched;
        this.family = family;
        this.role = role;
    }


    public void updateFamily(Family family) {
        this.family = family;
    }

    public void updateIsMatched(IsMatched isMatched) {
        this.isMatched = isMatched;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateFamilyRole(FamilyRole familyRole) {
        this.familyRole = familyRole;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateAddress(String address) {
        this.address = address;
    }
}
