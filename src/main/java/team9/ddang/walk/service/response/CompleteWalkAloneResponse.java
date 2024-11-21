package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.walk.entity.Position;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "산책 혼자 완료 Response DTO")
public record CompleteWalkAloneResponse(
        @Schema(description = "날짜", example = "2024-11-21")
        LocalDate date,

        @Schema(description = "회원 이름", example = "문재경")
        String memberName,

        @Schema(description = "총 이동 거리 (미터)", example = "1200")
        int totalDistance,

        @Schema(description = "총 이동 시간 (초)", example = "1800")
        long totalMinute,

        @Schema(description = "총 소비 칼로리", example = "300")
        int totalCalorie,

        @Schema(description = "이동 경로 리스트")
        List<Position> positionList
) {

    public static CompleteWalkAloneResponse of(String memberName, int totalDistance, long totalMinute, int totalCalorie, List<Position> points){
        return new CompleteWalkAloneResponse(LocalDate.now(), memberName, totalDistance, totalMinute, totalCalorie, points);
    }
}
