package team9.ddang.member.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.IntegrationTestSupport;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.*;
import team9.ddang.member.exception.MemberExceptionMessage;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.ReportRepository;
import team9.ddang.member.service.request.ReportServiceRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ReportServiceImplTest extends IntegrationTestSupport {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReportRepository reportRepository;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void setUp() {
        sender = Member.builder()
                .name("Sender")
                .email("sender@example.com")
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .address("123 Sender Street")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(sender);

        receiver = Member.builder()
                .name("Receiver")
                .email("receiver@example.com")
                .gender(Gender.FEMALE)
                .provider(Provider.GOOGLE)
                .address("123 Receiver Street")
                .isMatched(IsMatched.FALSE)
                .role(Role.USER)
                .familyRole(FamilyRole.FATHER)
                .profileImg("test profile img")
                .build();
        memberRepository.save(receiver);
    }

    @Test
    @DisplayName("신고를 성공적으로 생성해야 한다")
    void createReport_Success() {
        ReportServiceRequest request = new ReportServiceRequest(receiver.getMemberId(), "신고 사유");

        reportService.createReport(sender, request);

        em.flush();
        em.clear();

        Report savedReport = reportRepository.findAll().get(0);
        assertThat(savedReport).isNotNull();
        assertThat(savedReport.getReason()).isEqualTo("신고 사유");
    }

    @Test
    @DisplayName("본인을 신고하려 하면 예외를 발생시켜야 한다")
    void createReport_SelfReport_ThrowsException() {
        ReportServiceRequest request = new ReportServiceRequest(sender.getMemberId(), "신고 사유");

        assertThatThrownBy(() -> reportService.createReport(sender, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MemberExceptionMessage.MEMBER_NOT_REPORT_SELF.getText());
    }

    @Test
    @DisplayName("존재하지 않는 회원을 신고하려 하면 예외를 발생시켜야 한다")
    void createReport_NonExistentMember_ThrowsException() {
        ReportServiceRequest request = new ReportServiceRequest(999L, "신고 사유");

        assertThatThrownBy(() -> reportService.createReport(sender, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getText());
    }
}