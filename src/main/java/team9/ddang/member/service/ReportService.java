package team9.ddang.member.service;

import team9.ddang.member.entity.Member;
import team9.ddang.member.service.request.ReportServiceRequest;

public interface ReportService {

    void createReport(Member member, ReportServiceRequest request);
}
