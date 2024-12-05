package team9.ddang.family.service;

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
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.entity.Family;
import team9.ddang.family.entity.WalkSchedule;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;
import team9.ddang.family.service.request.WalkScheduleDeleteServiceRequest;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.global.entity.Gender;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.*;
import team9.ddang.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class WalkScheduleServiceImplTest extends IntegrationTestSupport {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private WalkScheduleServiceImpl walkScheduleService;

    @Autowired
    private WalkScheduleRepository walkScheduleRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Family testFamily;
    private Member testMember;
    private Member walkMember;
    private Dog testDog;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .address("123 Test Street")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(testMember);

        walkMember = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .gender(Gender.FEMALE)
                .provider(Provider.GOOGLE)
                .address("123 Test Street")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.MOTHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(walkMember);

        testFamily = Family.builder()
                .familyName("Test Family")
                .member(testMember)
                .build();
        familyRepository.save(testFamily);

        testMember.updateFamily(testFamily);
        walkMember.updateFamily(testFamily);
        memberRepository.save(testMember);
        memberRepository.save(walkMember);

        testDog = Dog.builder()
                .name("Buddy")
                .breed("Golden Retriever")
                .birthDate(LocalDate.of(2020, 5, 20))
                .gender(Gender.MALE)
                .weight(BigDecimal.valueOf(3.0))
                .family(testFamily)
                .comment("Loves to play fetch!")
                .isNeutered(IsNeutered.TRUE)
                .build();
        em.persist(testDog);
    }

    @Test
    @DisplayName("산책 일정을 생성해야 한다")
    void createWalkSchedule_Success() {

        // Given
        List<DayOfWeek> dayOfWeeks = List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY
        );

        WalkScheduleCreateServiceRequest request = new WalkScheduleCreateServiceRequest(
                LocalTime.of(9, 30),
                dayOfWeeks
        );

        // When
        List<WalkScheduleResponse> responses = walkScheduleService.createWalkSchedule(request, testMember);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(3); // 요일별로 3개의 응답이 생성되어야 함
        WalkScheduleResponse firstResponse = responses.get(0);

        assertThat(firstResponse.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(firstResponse.walkTime()).isEqualTo(LocalTime.of(9, 30));
        assertThat(firstResponse.memberName()).isEqualTo(testMember.getName());
        assertThat(firstResponse.dogName()).isEqualTo(testDog.getName());
    }

    @Test
    @DisplayName("가족 ID로 산책 일정을 조회해야 한다")
    void getWalkSchedulesByFamilyId_Success() {
        WalkSchedule schedule = WalkSchedule.builder()
                .member(walkMember)
                .dog(testDog)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .walkTime(LocalTime.of(18, 0))
                .family(testFamily)
                .build();
        walkScheduleRepository.save(schedule);

        List<WalkScheduleResponse> schedules = walkScheduleService.getWalkSchedulesByFamilyId(testMember);

        assertThat(schedules).isNotNull();
        assertThat(schedules).hasSize(1);
        assertThat(schedules.get(0).dayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(schedules.get(0).walkTime()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    @DisplayName("산책 일정을 삭제해야 한다")
    void deleteWalkSchedule_Success() {
        WalkSchedule schedule = WalkSchedule.builder()
                .member(walkMember)
                .dog(testDog)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .walkTime(LocalTime.of(7, 0))
                .family(testFamily)
                .build();
        walkScheduleRepository.save(schedule);

        List<Long> ids = new ArrayList<>();
        ids.add(schedule.getWalkScheduleId());

        WalkScheduleDeleteServiceRequest walkScheduleDeleteServiceRequest = new WalkScheduleDeleteServiceRequest(ids);

        walkScheduleService.deleteWalkSchedule(walkScheduleDeleteServiceRequest, walkMember);

        em.flush();
        em.clear();

        boolean exists = walkScheduleRepository.existsById(schedule.getWalkScheduleId());
        assertThat(exists).isFalse();
    }
}
