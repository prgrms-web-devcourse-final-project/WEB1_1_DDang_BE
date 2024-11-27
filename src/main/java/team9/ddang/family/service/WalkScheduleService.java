package team9.ddang.family.service;

import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;
import team9.ddang.family.service.response.WalkScheduleResponse;
import team9.ddang.member.entity.Member;

public interface WalkScheduleService {
    WalkScheduleResponse createWalkSchedule(WalkScheduleCreateServiceRequest request, Member member);
}
