package team9.ddang.walk.service.response.walk;

import team9.ddang.walk.service.request.walk.StartWalkServiceRequest;

public record WalkWithResponse(
        String email,
        double latitude,
        double longitude
) {
    public static WalkWithResponse of(String email, StartWalkServiceRequest serviceRequest){
        return new WalkWithResponse(email, serviceRequest.latitude(), serviceRequest.longitude());
    }
}
