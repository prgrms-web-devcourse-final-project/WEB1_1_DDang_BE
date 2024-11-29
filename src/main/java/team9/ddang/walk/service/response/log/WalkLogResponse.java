package team9.ddang.walk.service.response.log;

import team9.ddang.walk.entity.Position;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.service.response.TimeDuration;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "산책 기록 응답")
public record WalkLogResponse(
        @Schema(description = "위치 리스트", example = "[{lat: 37.5665, lng: 126.9780}]")
        List<Position> points,

        TimeDuration timeDuration,

        @Schema(description = "소모 칼로리", example = "100")
        int totalCalorie,

        @Schema(description = "총 거리(미터)", example = "5000")
        int totalDistanceMeter
) {
    public static WalkLogResponse of(List<Position> points, Walk walk, int totalCalorie) {
        return new WalkLogResponse(
                points,
                TimeDuration.of(walk.getStartTime(), walk.getEndTime()),
                totalCalorie,
                walk.getTotalDistance()
        );
    }
}

