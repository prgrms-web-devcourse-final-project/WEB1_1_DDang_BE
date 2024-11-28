package team9.ddang.walk.service.response.log;

import team9.ddang.walk.entity.Position;
import team9.ddang.walk.entity.Walk;
import team9.ddang.walk.service.response.TimeDuration;

import java.util.List;

public record WalkLogResponse(
        List<Position> points,
        TimeDuration timeDuration,
        int totalCalorie,
        int totalDistanceMeter
) {
    public static WalkLogResponse of(List<Position> points, Walk walk, int totalCalorie){
        return new WalkLogResponse(
                points,
                TimeDuration.of(walk.getStartTime(), walk.getEndTime()),
                totalCalorie,
                walk.getTotalDistance());
    }


}
