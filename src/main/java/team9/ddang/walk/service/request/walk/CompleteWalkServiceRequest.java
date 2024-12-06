package team9.ddang.walk.service.request.walk;

import team9.ddang.member.entity.Member;
import team9.ddang.walk.entity.Location;
import team9.ddang.walk.entity.Walk;

import java.util.List;

public record CompleteWalkServiceRequest(
        Integer totalDistance,
        Long totalWalkTime
) {

    public Walk toEntity(List<Location> locations, Member member, String walkImg){
        return  Walk.builder()
                .totalDistance(totalDistance)
                .member(member)
                .walkImg(walkImg)
                .startTime(locations.get(0).getPosition().getTimeStamp())
                .endTime(locations.get(locations.size()-1).getPosition().getTimeStamp())
                .build();

    }
}
