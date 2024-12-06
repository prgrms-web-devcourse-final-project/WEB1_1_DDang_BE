package team9.ddang.family.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.entity.Family;
import team9.ddang.family.entity.WalkSchedule;
import team9.ddang.global.entity.Gender;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class WalkScheduleRepositoryTest extends IntegrationTestSupport {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private WalkScheduleRepository walkScheduleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private FamilyRepository familyRepository;

    private Family testFamily;
    private Member testMember;
    private WalkSchedule testSchedule;
    private Dog testDog;

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

        testMember.updateFamily(testFamily);
        memberRepository.save(testMember);

        testDog = Dog.builder()
                .name("Buddy")
                .breed("Golden Retriever")
                .birthDate(LocalDate.of(2020, 5, 20))
                .gender(Gender.MALE)
                .weight(BigDecimal.valueOf(3.3))
                .isNeutered(IsNeutered.TRUE)
                .family(testFamily)
                .comment("Loves to play fetch!")
                .build();
        dogRepository.save(testDog);

        testSchedule = WalkSchedule.builder()
                .member(testMember)
                .dayOfWeek(DayOfWeek.MONDAY)
                .dog(testDog)
                .walkTime(LocalTime.of(9, 30))
                .family(testFamily)
                .build();
        walkScheduleRepository.save(testSchedule);
    }

    @Test
    @DisplayName("findActiveById는 활성화된 산책 일정을 ID로 조회한다")
    void findActiveById_returnsActiveWalkSchedule() {
        Optional<WalkSchedule> foundSchedule = walkScheduleRepository.findActiveById(testSchedule.getWalkScheduleId());

        assertAll(
                () -> assertThat(foundSchedule).isPresent(),
                () -> assertThat(foundSchedule.get()).isEqualTo(testSchedule),
                () -> assertThat(foundSchedule.get().getIsDeleted()).isEqualTo(IsDeleted.FALSE)
        );
    }

    @Test
    @DisplayName("findActiveById는 삭제된 산책 일정을 조회하지 않는다")
    void findActiveById_doesNotReturnDeletedWalkSchedule() {
        walkScheduleRepository.deleteById(testSchedule.getWalkScheduleId());

        Optional<WalkSchedule> foundSchedule = walkScheduleRepository.findActiveById(testSchedule.getWalkScheduleId());

        assertAll(
                () -> assertThat(foundSchedule).isNotPresent()
        );
    }

    @Test
    @DisplayName("deleteById는 산책 일정을 완전히 삭제한다")
    void hardDeleteById_deletesWalkSchedule() {
        walkScheduleRepository.deleteById(testSchedule.getWalkScheduleId());

        em.flush();
        em.clear();

        Optional<WalkSchedule> deletedSchedule = walkScheduleRepository.findById(testSchedule.getWalkScheduleId());

        assertAll(
                () -> assertThat(deletedSchedule).isEmpty()
        );
    }

    @Test
    @DisplayName("findAllByFamilyId는 가족 ID로 활성화된 산책 일정을 조회한다")
    void findAllByFamilyId_returnsActiveWalkSchedules() {
        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyId(testFamily.getFamilyId());

        assertAll(
                () -> assertThat(schedules).isNotEmpty(),
                () -> assertThat(schedules).hasSize(1),
                () -> assertThat(schedules.get(0)).isEqualTo(testSchedule)
        );
    }

    @Test
    @DisplayName("deleteByMemberId는 멤버의 모든 산책 일정을 완전히 삭제한다")
    void hardDeleteByMemberId_deletesSchedulesForMember() {
        walkScheduleRepository.deleteByMemberId(testMember.getMemberId());

        em.flush();
        em.clear();

        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyId(testFamily.getFamilyId());
        Optional<WalkSchedule> deletedSchedule = walkScheduleRepository.findById(testSchedule.getWalkScheduleId());

        assertAll(
                () -> assertThat(schedules).isEmpty(),
                () -> assertThat(deletedSchedule).isEmpty()
        );
    }

    @Test
    @DisplayName("deleteByFamilyId는 가족의 모든 산책 일정을 완전히 삭제한다")
    void hardDeleteByFamilyId_deletesSchedulesForFamily() {
        walkScheduleRepository.deleteByFamilyId(testFamily.getFamilyId());

        em.flush();
        em.clear();

        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyId(testFamily.getFamilyId());
        Optional<WalkSchedule> deletedSchedule = walkScheduleRepository.findById(testSchedule.getWalkScheduleId());

        assertAll(
                () -> assertThat(schedules).isEmpty(),
                () -> assertThat(deletedSchedule).isEmpty()
        );
    }
}
