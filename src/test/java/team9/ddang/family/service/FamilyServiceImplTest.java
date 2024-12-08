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
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.service.response.FamilyDetailResponse;
import team9.ddang.family.service.response.FamilyDogResponse;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.global.entity.Gender;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.*;
import team9.ddang.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class FamilyServiceImplTest extends IntegrationTestSupport {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private FamilyServiceImpl familyService;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private MemberDogRepository memberDogRepository;

    private Member testMember;
    private Dog testDog;
    private Family testFamily;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .gender(Gender.MALE)
                .address("123 Test Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(testMember);

        testFamily = Family.builder()
                .familyName("Test Family")
                .member(testMember)
                .build();
        familyRepository.save(testFamily);

        testDog = Dog.builder()
                .name("Buddy")
                .breed("Golden Retriever")
                .birthDate(LocalDate.of(2020, 5, 20))
                .gender(Gender.MALE)
                .weight(BigDecimal.valueOf(3.0))
                .isNeutered(IsNeutered.TRUE)
                .family(testFamily)
                .comment("Loves to play fetch!")
                .build();
        dogRepository.save(testDog);

        testMember.updateFamily(testFamily);
        memberRepository.save(testMember);
    }

    @Test
    @DisplayName("가족을 생성해야 한다")
    void createFamily_Success() {
        Member newMember = Member.builder()
                .name("New Member")
                .email("new.member@example.com")
                .gender(Gender.FEMALE)
                .address("456 Another Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(newMember);

        MemberDog memberDog = MemberDog.builder()
                .member(newMember)
                .dog(testDog)
                .build();
        memberDogRepository.save(memberDog);

        FamilyResponse response = familyService.createFamily(newMember);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.memberId()).isEqualTo(newMember.getMemberId())
        );
    }

    @Test
    @DisplayName("가족 정보 조회에 성공해야 한다")
    void getMyFamily_Success() {
        FamilyDetailResponse response = familyService.getMyFamily(testMember);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.members()).hasSize(1),
                () -> assertThat(response.dogs()).hasSize(1)
        );
    }

    @Test
    @DisplayName("가족 탈퇴에 성공해야 한다")
    void leaveFamily_Success() {
        Member additionalMember = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .gender(Gender.FEMALE)
                .address("789 Test Avenue")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        additionalMember.updateFamily(testFamily);
        memberRepository.save(additionalMember);

        familyService.leaveFamily(additionalMember);

        Member updatedMember = memberRepository.findById(additionalMember.getMemberId()).orElseThrow();

        assertAll(
                () -> assertThat(updatedMember).isNotNull(),
                () -> assertThat(updatedMember.getFamily()).isNull()
        );
    }

    @Test
    @DisplayName("가족 삭제에 성공해야 한다")
    void deleteFamily_Success() {
        familyService.deleteFamily(testMember);

        em.flush();
        em.clear();

        Optional<Family> deletedFamily = familyRepository.findById(testFamily.getFamilyId());

        assertAll(
                () -> assertThat(deletedFamily).isPresent(),
                () -> assertThat(deletedFamily.get().getIsDeleted()).isEqualTo(IsDeleted.TRUE)
        );
    }

    @Test
    @DisplayName("가족 초대 코드를 생성해야 한다")
    void createInviteCode_Success() {
        var response = familyService.createInviteCode(testMember);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.inviteCode()).isNotEmpty(),
                () -> assertThat(response.expiresInSeconds()).isGreaterThan(0)
        );
    }

    @Test
    @DisplayName("초대 코드로 가족에 멤버를 추가해야 한다")
    void addMemberToFamily_Success() {
        String inviteCode = familyService.createInviteCode(testMember).inviteCode();

        Member newMember = Member.builder()
                .name("New Member")
                .email("new.member@example.com")
                .gender(Gender.FEMALE)
                .address("456 Another Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(newMember);

        FamilyResponse response = familyService.addMemberToFamily(inviteCode, newMember);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(newMember.getFamily()).isEqualTo(testFamily)
        );
    }

    @Test
    @DisplayName("초대 코드로 가족의 강아지 정보를 조회해야 한다")
    void getFamilyDogs_Success() {
        var response = familyService.createInviteCode(testMember);

        String inviteCode = response.inviteCode();
        Member requester = Member.builder()
                .name("New Member")
                .email("new.member@example.com")
                .gender(Gender.FEMALE)
                .address("456 Another Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(requester);

        List<FamilyDogResponse> dogs = familyService.getFamilyDogs(inviteCode, requester);

        assertAll(
                () -> assertThat(dogs).isNotNull(),
                () -> assertThat(dogs).hasSize(1),
                () -> assertThat(dogs.get(0).dogId()).isEqualTo(testDog.getDogId()),
                () -> assertThat(dogs.get(0).name()).isEqualTo(testDog.getName()),
                () -> assertThat(dogs.get(0).breed()).isEqualTo(testDog.getBreed())
        );
    }

    @Test
    @DisplayName("유효하지 않은 초대 코드로 강아지 정보를 조회하면 예외가 발생해야 한다")
    void getFamilyDogs_ShouldThrowException_WhenInvalidInviteCode() {
        String invalidInviteCode = "INVALID_CODE";
        Member requester = Member.builder()
                .name("New Member")
                .email("new.member@example.com")
                .gender(Gender.FEMALE)
                .address("456 Another Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(requester);

        assertThatThrownBy(() -> familyService.getFamilyDogs(invalidInviteCode, requester))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(FamilyExceptionMessage.INVALID_INVITE_CODE.getText());
    }

    @Test
    @DisplayName("멤버를 가족에서 제거해야 한다")
    void removeMemberFromFamily_Success() {
        Member additionalMember = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .gender(Gender.FEMALE)
                .address("789 Test Avenue")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        additionalMember.updateFamily(testFamily);
        memberRepository.save(additionalMember);

        familyService.removeMemberFromFamily(additionalMember.getMemberId(), testMember);

        Member updatedMember = memberRepository.findById(additionalMember.getMemberId()).orElseThrow();

        assertAll(
                () -> assertThat(updatedMember).isNotNull(),
                () -> assertThat(updatedMember.getFamily()).isNull()
        );
    }

    @Test
    @DisplayName("이미 가족에 속한 멤버가 가족을 생성하려고 하면 예외가 발생해야 한다")
    void createFamily_ShouldThrowException_WhenMemberAlreadyInFamily() {
        assertAll(
                () -> assertThatThrownBy(() -> familyService.createFamily(testMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_ALREADY_IN_FAMILY.getText())
        );
    }

    @Test
    @DisplayName("가족에 속하지 않은 멤버가 가족 정보를 조회하려고 하면 예외가 발생해야 한다")
    void getMyFamily_ShouldThrowException_WhenMemberNotInFamily() {
        Member nonFamilyMember = Member.builder()
                .name("Non Family Member")
                .email("non.family@example.com")
                .gender(Gender.MALE)
                .address("456 Unknown Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("no-profile.jpg")
                .build();
        memberRepository.save(nonFamilyMember);

        assertAll(
                () -> assertThatThrownBy(() -> familyService.getMyFamily(nonFamilyMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_NOT_IN_FAMILY.getText())
        );
    }

    @Test
    @DisplayName("가족 Boss가 가족을 탈퇴하려고 하면 예외가 발생해야 한다")
    void leaveFamily_ShouldThrowException_WhenBossTriesToLeave() {
        assertAll(
                () -> assertThatThrownBy(() -> familyService.leaveFamily(testMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_NOT_LEAVE_OWNER.getText())
        );
    }

    @Test
    @DisplayName("가족에 멤버가 남아 있는 상태에서 가족을 삭제하려고 하면 예외가 발생해야 한다")
    void deleteFamily_ShouldThrowException_WhenFamilyNotEmpty() {
        Member additionalMember = Member.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .gender(Gender.FEMALE)
                .address("789 Test Avenue")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        additionalMember.updateFamily(testFamily);
        memberRepository.save(additionalMember);

        assertAll(
                () -> assertThatThrownBy(() -> familyService.deleteFamily(testMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.FAMILY_NOT_EMPTY.getText())
        );
    }

    @Test
    @DisplayName("유효하지 않은 초대 코드로 가족에 멤버를 추가하려고 하면 예외가 발생해야 한다")
    void addMemberToFamily_ShouldThrowException_WhenInviteCodeInvalid() {
        Member newMember = Member.builder()
                .name("New Member")
                .email("new.member@example.com")
                .gender(Gender.FEMALE)
                .address("456 Another Street")
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .isMatched(IsMatched.FALSE)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(newMember);

        assertAll(
                () -> assertThatThrownBy(() -> familyService.addMemberToFamily("INVALID_CODE", newMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.INVALID_INVITE_CODE.getText())
        );
    }

    @Test
    @DisplayName("가족 Boss를 가족에서 제거하려고 하면 예외가 발생해야 한다")
    void removeMemberFromFamily_ShouldThrowException_WhenTryingToRemoveBoss() {
        assertAll(
                () -> assertThatThrownBy(() -> familyService.removeMemberFromFamily(testMember.getMemberId(), testMember))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(FamilyExceptionMessage.MEMBER_FAMILY_BOSS.getText())
        );
    }
}