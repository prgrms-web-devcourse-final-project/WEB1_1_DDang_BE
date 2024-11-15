package team9.ddang.family.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.ddang.global.entity.BaseEntity;

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

    @Builder
    private Family(String familyName, String familyCode) {
        this.familyName = familyName;
        this.familyCode = familyCode;
    }
}
// TODO : 나중에 더 추가할 예정