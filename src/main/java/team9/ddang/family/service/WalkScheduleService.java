package team9.ddang.family.service;

import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.entity.Member;

import java.util.List;

public interface WalkScheduleService {
    WalkScheduleResponse createWalkSchedule(WalkScheduleCreateServiceRequest request, Member member);

    List<WalkScheduleResponse> getWalkSchedulesByFamilyId(Member member);

    void deleteWalkSchedule(Long walkScheduleId, Member member);
}
