package team9.ddang.walk.service;

import team9.ddang.walk.service.request.StartWalkServiceRequest;

public interface WalkLocationService {

    void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest);
}
