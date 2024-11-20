package team9.ddang.walk.service.response;

import team9.ddang.walk.entity.Position;

import java.time.LocalDate;
import java.util.List;

public record CompleteWalkAloneResponse(
        LocalDate date,
        String memberName,
        int totalDistance,
        long totalMinute,
        int totalCalorie,
        List<Position> positionList
) {

    public static CompleteWalkAloneResponse of(String memberName, int totalDistance, long totalMinute, int totalCalorie, List<Position> points){
        return new CompleteWalkAloneResponse(LocalDate.now(), memberName, totalDistance, totalMinute, totalCalorie, points);
    }
}
