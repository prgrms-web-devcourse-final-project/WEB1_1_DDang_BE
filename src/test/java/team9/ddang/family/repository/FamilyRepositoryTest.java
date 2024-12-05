package team9.ddang.family.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.family.entity.Family;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class FamilyRepositoryTest extends IntegrationTestSupport {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private Family testFamily;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .gender(team9.ddang.global.entity.Gender.MALE)
                .address("123 Test Street")
                .provider(Provider.GOOGLE)
                .role(team9.ddang.member.entity.Role.USER)
                .isMatched(IsMatched.TRUE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(testMember);

        testFamily = Family.builder()
                .familyName("Test Family")
                .member(testMember)
                .build();
        familyRepository.save(testFamily);
    }

    @Test
    @DisplayName("findActiveById는 활성화된 가족을 ID로 조회한다")
    void findActiveById_returnsActiveFamily() {
        Optional<Family> foundFamily = familyRepository.findActiveById(testFamily.getFamilyId());

        assertThat(foundFamily).isPresent();
        assertThat(foundFamily.get()).isEqualTo(testFamily);
        assertThat(foundFamily.get().getIsDeleted()).isEqualTo(IsDeleted.FALSE);
    }

    @Test
    @DisplayName("findActiveById는 삭제된 가족을 조회하지 않는다")
    void findActiveById_doesNotReturnDeletedFamily() {
        familyRepository.softDeleteFamilyById(testFamily.getFamilyId());

        Optional<Family> foundFamily = familyRepository.findActiveById(testFamily.getFamilyId());

        assertThat(foundFamily).isNotPresent();
    }

    @Test
    @DisplayName("softDeleteFamilyById는 가족을 소프트 삭제 처리한다")
    void softDeleteFamilyById_softDeletesFamily() {
        familyRepository.softDeleteFamilyById(testFamily.getFamilyId());

        em.flush();
        em.clear();

        Optional<Family> deletedFamily = familyRepository.findById(testFamily.getFamilyId());
        assertThat(deletedFamily).isPresent();
        assertThat(deletedFamily.get().getIsDeleted()).isEqualTo(IsDeleted.TRUE);
    }
}