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
import team9.ddang.family.exception.FamilyExceptionMessage;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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

        List<DayOfWeek> dayOfWeeks = List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY
        );

        WalkScheduleCreateServiceRequest request = new WalkScheduleCreateServiceRequest(
                LocalTime.of(9, 30),
                dayOfWeeks
        );

        List<WalkScheduleResponse> responses = walkScheduleService.createWalkSchedule(request, testMember);

        assertAll(
                () -> assertThat(responses).isNotNull(),
                () -> assertThat(responses).hasSize(3),
                () -> {
                    WalkScheduleResponse firstResponse = responses.get(0);
                    assertAll(
                            () -> assertThat(firstResponse.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY),
                            () -> assertThat(firstResponse.walkTime()).isEqualTo(LocalTime.of(9, 30)),
                            () -> assertThat(firstResponse.memberName()).isEqualTo(testMember.getName()),
                            () -> assertThat(firstResponse.dogName()).isEqualTo(testDog.getName())
                    );
                }
        );
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

        assertAll(
                () -> assertThat(schedules).isNotNull(),
                () -> assertThat(schedules).hasSize(1),
                () -> assertAll(
                        () -> assertThat(schedules.get(0).dayOfWeek()).isEqualTo(DayOfWeek.TUESDAY),
                        () -> assertThat(schedules.get(0).walkTime()).isEqualTo(LocalTime.of(18, 0))
                )
        );
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
        assertAll(
                () -> assertThat(exists).isFalse()
        );
    }


    @Test
    @DisplayName("산책 일정 생성 실패 케이스를 검증한다")
    void createWalkSchedule_FailureCases() {
        WalkScheduleCreateServiceRequest emptyDayRequest = new WalkScheduleCreateServiceRequest(
                LocalTime.of(9, 30),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
        );

        Member nonFamilyMember = Member.builder()
                .name("Non Family Member")
                .email("nonfamily@example.com")
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .address("No Address")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("profile.jpg")
                .build();
        memberRepository.save(nonFamilyMember);

        assertAll(
                () -> assertThatThrownBy(() -> walkScheduleService.createWalkSchedule(emptyDayRequest, nonFamilyMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText())
        );
    }


    @Test
    @DisplayName("산책 일정 조회 실패를 검증한다")
    void getWalkSchedulesByFamilyId_FailureAndEdgeCases() {
        Member nonFamilyMember = Member.builder()
                .name("Non Family Member")
                .email("nonfamily@example.com")
                .gender(Gender.FEMALE)
                .provider(Provider.GOOGLE)
                .address("No Address")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("profile.jpg")
                .build();
        memberRepository.save(nonFamilyMember);

        List<WalkScheduleResponse> schedules = walkScheduleService.getWalkSchedulesByFamilyId(testMember);

        assertAll(
                () -> assertThat(schedules).isEmpty(),
                () -> assertThatThrownBy(() -> walkScheduleService.getWalkSchedulesByFamilyId(nonFamilyMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText())
        );
    }



    @Test
    @DisplayName("다른 사람의 산책 일정을 삭제하려고 하면 예외가 발생해야 한다")
    void deleteWalkSchedule_ShouldThrowException_WhenNotOwner() {
        WalkSchedule schedule = WalkSchedule.builder()
                .member(walkMember)
                .dog(testDog)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .walkTime(LocalTime.of(10, 0))
                .family(testFamily)
                .build();
        walkScheduleRepository.save(schedule);

        List<Long> scheduleIds = List.of(schedule.getWalkScheduleId());
        WalkScheduleDeleteServiceRequest request = new WalkScheduleDeleteServiceRequest(scheduleIds);

        assertThatThrownBy(() -> walkScheduleService.deleteWalkSchedule(request, testMember))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(FamilyExceptionMessage.WALKSCHEDULE_NOT_IN_FAMILY.getText());
    }



    @Test
    @DisplayName("멤버 검증 실패 케이스를 검증한다")
    void validateMemberInFamily_FailureCases() {
        Member invalidMember = Member.builder()
                .name("Invalid Member")
                .email("invalid@example.com")
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .address("Invalid Address")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("profile.jpg")
                .build();

        Member nonFamilyMember = Member.builder()
                .name("Non Family Member")
                .email("nonfamily@example.com")
                .gender(Gender.FEMALE)
                .provider(Provider.GOOGLE)
                .address("No Address")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("profile.jpg")
                .build();
        memberRepository.save(nonFamilyMember);

        assertAll(
                () -> assertThatThrownBy(() -> walkScheduleService.getWalkSchedulesByMemberId(invalidMember.getMemberId(), testMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_NOT_FOUND.getText()),

                () -> assertThatThrownBy(() -> walkScheduleService.getWalkSchedulesByMemberId(nonFamilyMember.getMemberId(), testMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText())
        );
    }
}
