package team9.ddang.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.member.entity.Member;
import team9.ddang.member.entity.Report;
import team9.ddang.member.exception.MemberExceptionMessage;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.ReportRepository;
import team9.ddang.member.service.request.ReportServiceRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createReport(Member member, ReportServiceRequest request) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        Member reportMember = findMemberByIdOrThrowException(request.receiverId());

        if(currentMember.getMemberId().equals(reportMember.getMemberId())) {
            throw new IllegalArgumentException(MemberExceptionMessage.MEMBER_NOT_REPORT_SELF.getText());
        }

        Report report = Report.builder()
                .sender(currentMember)
                .receiver(reportMember)
                .reason(request.reason())
                .build();

        reportRepository.save(report);
    }


    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, MemberExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(MemberExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }
}
