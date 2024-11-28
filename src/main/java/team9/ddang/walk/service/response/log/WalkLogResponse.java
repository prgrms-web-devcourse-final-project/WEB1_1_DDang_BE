package team9.ddang.walk.service.response.log;

import team9.ddang.walk.entity.Position;
import team9.ddang.walk.entity.Walk;

import java.time.temporal.ChronoUnit;
import java.util.List;

public record WalkLogResponse(
        List<Position> points,
        long totalMinute,
        int totalCalorie,
        int totalDistanceMeter
) {
    public static WalkLogResponse of(List<Position> points, Walk walk, int totalCalorie){
        return new WalkLogResponse(
                points,
                ChronoUnit.MINUTES.between(walk.getStartTime(), walk.getEndTime()),
                totalCalorie,
                walk.getTotalDistance());

    }


}
