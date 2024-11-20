package team9.ddang.walk.service;

import team9.ddang.walk.service.request.CompleteWalkServiceRequest;
import team9.ddang.walk.service.response.CompleteWalkAloneResponse;

public interface WalkService {

    CompleteWalkAloneResponse completeWalk(Long number, CompleteWalkServiceRequest completeWalkServiceRequest);
}
