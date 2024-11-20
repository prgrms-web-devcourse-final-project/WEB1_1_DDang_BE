package team9.ddang.walk.controller.request;

import jakarta.validation.constraints.NotNull;
import team9.ddang.walk.service.request.CompleteWalkServiceRequest;

// TODO : 아직 request 추가가 좀 더 이루어져야 할 수도 있음.
public record CompleteWalkRequest(
        @NotNull(message = "산책 총 거리가 존재해야 합니다.")
        Integer totalDistance,
        @NotNull(message = "산책 총 시간이 존재해야 합니다.")
        Long totalWalkTime
) {
    public CompleteWalkServiceRequest toServiceRequest(){
        return new CompleteWalkServiceRequest(totalDistance, totalWalkTime);
    }
}
