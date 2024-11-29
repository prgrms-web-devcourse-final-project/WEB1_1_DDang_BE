package team9.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.entity.WalkSchedule;
import team9.ddang.member.entity.Member;
import team9.ddang.dog.entity.Dog;

import java.time.LocalTime;

@Schema(description = "산책 일정 응답 데이터")
public record WalkScheduleResponse(

        @Schema(description = "산책 일정 ID", example = "1")
        Long walkScheduleId,

        @Schema(description = "산책 요일", example = "MON")
        DayOfWeek dayOfWeek,

        @Schema(description = "산책 시간", example = "10:00")
        LocalTime walkTime,

        @Schema(description = "멤버 이름", example = "John Doe")
        String memberName,

        @Schema(description = "강아지 이름", example = "Buddy")
        String dogName,

        @Schema(description = "가족 ID", example = "101")
        Long familyId
) {
    public static WalkScheduleResponse from(WalkSchedule walkSchedule) {
        Member member = walkSchedule.getMember();
        Dog dog = walkSchedule.getDog();

        return new WalkScheduleResponse(
                walkSchedule.getWalkScheduleId(),
                walkSchedule.getDayOfWeek(),
                walkSchedule.getWalkTime(),
                member.getName(),
                dog.getName(),
                walkSchedule.getFamily().getFamilyId()
        );
    }
}