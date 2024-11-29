package team9.ddang.walk.service;

import team9.ddang.member.entity.Member;
import team9.ddang.walk.service.request.walk.CompleteWalkServiceRequest;
import team9.ddang.walk.service.response.walk.CompleteWalkResponse;

public interface WalkService {

    CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest);
}
