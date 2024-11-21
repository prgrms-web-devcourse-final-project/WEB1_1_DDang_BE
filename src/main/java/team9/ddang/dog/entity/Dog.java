package team9.ddang.dog.entity;

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
public class Dog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dogId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String breed;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Integer weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsNeutered isNeutered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Builder
    private Dog(String name, String breed, LocalDate birthDate, Gender gender, Integer weight, IsNeutered isNeutered, String profileImg, Family family) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.profileImg = profileImg;
        this.isNeutered = isNeutered;
        this.family = family;
    }
}
