package team9.ddang.family.controller.request;

import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import team9.ddang.family.entity.DayOfWeek;
import team9.ddang.family.service.request.WalkScheduleCreateServiceRequest;

@Schema(description = "산책 일정 생성 데이터")
public record WalkScheduleCreateRequest(

        @Schema(description = "산책 시간 (HH:mm)", example = "09:30")
        @NotNull(message = "산책 시간을 입력해주세요.")
        LocalTime walkTime,

        @Schema(description = "요일 리스트", example = "[\"MONDAY\", \"WEDNESDAY\"]")
        @NotEmpty(message = "요일을 입력해주세요.")
        List<DayOfWeek> dayOfWeekList

//        @Schema(description = "강아지 ID", example = "5")
//        @NotNull(message = "강아지 ID를 입력해주세요.")
//        Long dogId
) {
    public WalkScheduleCreateServiceRequest toServiceRequest() {
        return new WalkScheduleCreateServiceRequest(walkTime, dayOfWeekList);
    }
}
