package team9.ddang.walk.service.response.log;

import team9.ddang.walk.service.response.TimeDuration;

public record WalkStaticsResponse(
        TimeDuration timeDuration,
        int walkCount,
        int totalDistanceKilo
) {
    public static WalkStaticsResponse of(long totalSeconds, int walkCount, int totalDistanceKilo){
        return new WalkStaticsResponse(TimeDuration.from(totalSeconds), walkCount, totalDistanceKilo);
    }
}
