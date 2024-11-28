package team9.ddang.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.member.repository.ReportRepository;
import team9.ddang.member.service.request.ReportServiceRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public void createReport(Member member, ReportServiceRequest request){

    }
}
