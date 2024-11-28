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
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.entity.Role;
import team9.ddang.member.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(team9.ddang.global.entity.Gender.MALE)
                .address("123 Test Street")
                .provider(Provider.GOOGLE)
                .role(team9.ddang.member.entity.Role.USER)
                .isMatched(IsMatched.TRUE)
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
                .weight(30)
                .isNeutered(IsNeutered.TRUE)
                .family(testFamily)
                .comment("Loves to play fetch!")
                .build();
        dogRepository.save(testDog);

        testSchedule = WalkSchedule.builder()
                .member(testMember)
                .dayOfWeek(DayOfWeek.MON)
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

        assertThat(foundSchedule).isPresent();
        assertThat(foundSchedule.get()).isEqualTo(testSchedule);
        assertThat(foundSchedule.get().getIsDeleted()).isEqualTo(IsDeleted.FALSE);
    }

    @Test
    @DisplayName("findActiveById는 삭제된 산책 일정을 조회하지 않는다")
    void findActiveById_doesNotReturnDeletedWalkSchedule() {
        walkScheduleRepository.softDeleteById(testSchedule.getWalkScheduleId());

        Optional<WalkSchedule> foundSchedule = walkScheduleRepository.findActiveById(testSchedule.getWalkScheduleId());

        assertThat(foundSchedule).isNotPresent();
    }

    @Test
    @DisplayName("softDeleteById는 산책 일정을 소프트 삭제 처리한다")
    void softDeleteById_softDeletesWalkSchedule() {
        walkScheduleRepository.softDeleteById(testSchedule.getWalkScheduleId());

        em.flush();
        em.clear();

        Optional<WalkSchedule> deletedSchedule = walkScheduleRepository.findById(testSchedule.getWalkScheduleId());
        assertThat(deletedSchedule).isPresent();
        assertThat(deletedSchedule.get().getIsDeleted()).isEqualTo(IsDeleted.TRUE);
    }

    @Test
    @DisplayName("findAllByFamilyId는 가족 ID로 활성화된 산책 일정을 조회한다")
    void findAllByFamilyId_returnsActiveWalkSchedules() {
        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyId(testFamily.getFamilyId());

        assertThat(schedules).isNotEmpty();
        assertThat(schedules).hasSize(1);
        assertThat(schedules.get(0)).isEqualTo(testSchedule);
    }

    @Test
    @DisplayName("softDeleteByMemberId는 멤버의 모든 산책 일정을 소프트 삭제 처리한다")
    void softDeleteByMemberId_softDeletesSchedulesForMember() {
        walkScheduleRepository.softDeleteByMemberId(testMember.getMemberId());

        em.flush();
        em.clear();

        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyId(testFamily.getFamilyId());
        assertThat(schedules).isEmpty();

        Optional<WalkSchedule> deletedSchedule = walkScheduleRepository.findById(testSchedule.getWalkScheduleId());
        assertThat(deletedSchedule).isPresent();
        assertThat(deletedSchedule.get().getIsDeleted()).isEqualTo(IsDeleted.TRUE);
    }

    @Test
    @DisplayName("softDeleteByFamilyId는 가족의 모든 산책 일정을 소프트 삭제 처리한다")
    void softDeleteByFamilyId_softDeletesSchedulesForFamily() {
        walkScheduleRepository.softDeleteByFamilyId(testFamily.getFamilyId());

        em.flush();
        em.clear();

        List<WalkSchedule> schedules = walkScheduleRepository.findAllByFamilyId(testFamily.getFamilyId());
        assertThat(schedules).isEmpty();

        Optional<WalkSchedule> deletedSchedule = walkScheduleRepository.findById(testSchedule.getWalkScheduleId());
        assertThat(deletedSchedule).isPresent();
        assertThat(deletedSchedule.get().getIsDeleted()).isEqualTo(IsDeleted.TRUE);
    }
}
