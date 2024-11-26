package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.walk.entity.Position;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "산책 완료 Response DTO")
public record CompleteWalkResponse(
        @Schema(description = "날짜", example = "2024-11-21")
        LocalDate date,

        @Schema(description = "회원 이름", example = "김슝")
        String memberName,

        @Schema(description = "강아지 이름", example = "초코")
        String dogName,

        @Schema(description = "총 이동 거리 (미터)", example = "1200")
        int totalDistance,

        @Schema(description = "총 이동 시간 (초)", example = "1800")
        long totalMinute,

        @Schema(description = "총 소비 칼로리", example = "300")
        int totalCalorie,

        @Schema(description = "이동 경로 리스트")
        List<Position> positionList,

        @Schema(description = "같이 산책한 개 정보")
        WalkWithDogInfo walkWithDogInfo
) {

    public static CompleteWalkResponse of(String memberName, String dogName, int totalDistance,
                                          long totalMinute, int totalCalorie, List<Position> points, WalkWithDogInfo info){
        return new CompleteWalkResponse(LocalDate.now(), memberName,dogName, totalDistance, totalMinute, totalCalorie, points, info);
    }
}
