package team9.ddang.walk.service.response.log;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.walk.entity.Position;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.service.response.TimeDuration;

import java.util.List;

@Schema(description = "산책 기록 응답")
public record WalkLogResponse(
        @Schema(description = "산책 경로 이미지", example = "https://~~~~.com")
        String walkImg,

        @Schema(description = "산책 아이디", example = "1")
        Long walkId,

        TimeDuration timeDuration,

        @Schema(description = "소모 칼로리", example = "100")
        int totalCalorie,

        @Schema(description = "총 거리(미터)", example = "5000")
        int totalDistanceMeter,

        @Schema(description = "멤버 이름", example = "감자탕수육")
        String name,

        @Schema(description = "멤버 프로필 url", example = "/src/img/Avatar4.svg")
        String profileImg
) {
    public static WalkLogResponse of(List<Position> points, Walk walk, int totalCalorie) {
        return new WalkLogResponse(
                walk.getWalkImg(),
                walk.getWalkId(),
                team9.ddang.walk.service.response.TimeDuration.of(walk.getStartTime(), walk.getEndTime()),
                totalCalorie,
                walk.getTotalDistance(),
                walk.getMember().getName(),
                walk.getMember().getProfileImg()
        );
    }
}

