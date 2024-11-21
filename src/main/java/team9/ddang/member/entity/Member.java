package team9.ddang.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.family.entity.Family;
import team9.ddang.global.entity.BaseEntity;
import team9.ddang.global.entity.Gender;

import java.time.LocalDate;

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

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private FamilyRole familyRole;

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
    private Member(String name, String email, LocalDate birthDate, Gender gender, FamilyRole familyRole, Provider provider, String profileImg, IsMatched isMatched, Family family, Role role) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.familyRole = familyRole;
        this.provider = provider;
        this.profileImg = profileImg;
        this.isMatched = isMatched;
        this.family = family;
        this.role = role;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}
