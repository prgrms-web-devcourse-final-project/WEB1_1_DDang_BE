package team9.ddang.walk.util;

import team9.ddang.walk.entity.Location;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static team9.ddang.walk.util.LocationDistanceUtil.distance;

public class WalkCalculator {

    private WalkCalculator() {}

    public static int calculateCalorie(int weight, long totalDistance){
        return (int) (0.75 * weight * totalDistance);
    }

    public static long calculateTime(List<Location> locations){
        LocalDateTime startTime = locations.get(0).getPosition().getTimeStamp();
        LocalDateTime endTime = locations.get(locations.size()-1).getPosition().getTimeStamp();

        return Duration.between(startTime, endTime).toMinutes();
    }

    public static int calculateDistance(List<Location> locations){
        return (int) IntStream.range(0, locations.size() - 1)
                .mapToDouble(i -> distance(
                        locations.get(i).getPosition().getLatitude(),
                        locations.get(i).getPosition().getLongitude(),
                        locations.get(i + 1).getPosition().getLatitude(),
                        locations.get(i + 1).getPosition().getLongitude()
                ))
                .sum();
    }
}
