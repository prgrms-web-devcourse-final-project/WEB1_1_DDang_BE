package team9.ddang.family.service;

import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.entity.Member;

import java.util.List;

public interface WalkScheduleService {
    List<WalkScheduleResponse> createWalkSchedule(WalkScheduleCreateServiceRequest request, Member member);

    List<WalkScheduleResponse> getWalkSchedulesByFamilyId(Member member);

    List<WalkScheduleResponse> getWalkSchedulesByMemberId(Long memberId, Member member);

    void deleteWalkSchedule(Long walkScheduleId, Member member);
}
