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
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.service.response.FamilyDetailResponse;
import team9.ddang.family.service.response.FamilyResponse;
import team9.ddang.global.entity.Gender;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.*;
import team9.ddang.member.repository.MemberRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(newMember.getMemberId());
    }

    @Test
    @DisplayName("가족 정보 조회에 성공해야 한다")
    void getMyFamily_Success() {
        FamilyDetailResponse response = familyService.getMyFamily(testMember);

        assertThat(response).isNotNull();
        assertThat(response.members()).hasSize(1);
        assertThat(response.dogs()).hasSize(1);
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
        assertThat(updatedMember.getFamily()).isNull();
    }

    @Test
    @DisplayName("가족 삭제에 성공해야 한다")
    void deleteFamily_Success() {
        familyService.deleteFamily(testMember);

        em.flush();
        em.clear();

        Optional<Family> deletedFamily = familyRepository.findById(testFamily.getFamilyId());
        assertThat(deletedFamily).isPresent();
        assertThat(deletedFamily.get().getIsDeleted()).isEqualTo(IsDeleted.TRUE);
    }

    @Test
    @DisplayName("가족 초대 코드를 생성해야 한다")
    void createInviteCode_Success() {
        var response = familyService.createInviteCode(testMember);

        assertThat(response).isNotNull();
        assertThat(response.inviteCode()).isNotEmpty();
        assertThat(response.expiresInSeconds()).isGreaterThan(0);
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

        assertThat(response).isNotNull();
        assertThat(newMember.getFamily()).isEqualTo(testFamily);
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
        assertThat(updatedMember.getFamily()).isNull();
    }
}