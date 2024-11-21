package team9.ddang.walk.service;

import team9.ddang.walk.service.request.LocationServiceRequest;

public interface WalkLocationService {

    void startWalk(String email, LocationServiceRequest locationServiceRequest);
}
