package team9.ddang.walk.service;

import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.request.log.GetLogByDateServiceRequest;
import team9.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import team9.ddang.walk.service.response.log.WalkLogResponse;
import team9.ddang.walk.service.response.log.WalkStaticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface WalkLogService {
    List<LocalDate> getWalkLogs(Member member);

    List<WalkLogResponse> getWalkLogByDate(Member member, GetLogByDateServiceRequest service);

    List<Integer> getYearlyWalkLog(Member member);

    List<WalkLogByFamilyResponse> getYearlyWalkLogByFamily(Member member);

    WalkStaticsResponse getTotalWalkLog(Member member);

    WalkStaticsResponse getMonthlyTotalWalk(Member member);
}
