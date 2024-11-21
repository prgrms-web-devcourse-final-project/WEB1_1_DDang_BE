package team9.ddang.walk.controller.request;

import jakarta.validation.constraints.NotNull;
import team9.ddang.walk.service.request.CompleteWalkServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "산책 완료 Request DTO")
public record CompleteWalkRequest(

        @Schema(description = "산책 총 거리 (미터)", example = "1200")
        @NotNull(message = "산책 총 거리가 존재해야 합니다.")
        Integer totalDistance,

        @Schema(description = "산책 총 시간 (분)", example = "30")
        @NotNull(message = "산책 총 시간이 존재해야 합니다.")
        Long totalWalkTime
) {
    public CompleteWalkServiceRequest toServiceRequest() {
        return new CompleteWalkServiceRequest(totalDistance, totalWalkTime);
    }
}

