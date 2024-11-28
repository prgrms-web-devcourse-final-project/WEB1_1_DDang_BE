package team9.ddang.walk.service.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Schema(description = "시간 기간을 나타내는 클래스")
public record TimeDuration(

        @Schema(description = "시간 수", example = "1")
        long hours,
        @Schema(description = "분 수", example = "30")
        long minutes,
        @Schema(description = "초 수", example = "45")
        long seconds
) {

    public static TimeDuration from(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long remainingSeconds = totalSeconds % 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        return new TimeDuration(hours, minutes, seconds);
    }

    public static TimeDuration of(LocalDateTime startTime, LocalDateTime endTime) {
        return from(ChronoUnit.SECONDS.between(startTime, endTime));
    }
}
