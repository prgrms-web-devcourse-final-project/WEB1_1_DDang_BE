package team9.ddang.walk.service.response;

import team9.ddang.walk.service.request.StartWalkServiceRequest;

public record WalkWithResponse(
        String email,
        double latitude,
        double longitude
) {
    public static WalkWithResponse of(String email, StartWalkServiceRequest serviceRequest){
        return new WalkWithResponse(email, serviceRequest.latitude(), serviceRequest.longitude());
    }
}
