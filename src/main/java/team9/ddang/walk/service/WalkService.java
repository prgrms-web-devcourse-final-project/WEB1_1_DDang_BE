package team9.ddang.walk.service;

import team9.ddang.walk.service.request.CompleteWalkServiceRequest;
import team9.ddang.walk.service.response.CompleteWalkResponse;

public interface WalkService {

    CompleteWalkResponse completeWalk(Long number, CompleteWalkServiceRequest completeWalkServiceRequest);
}
