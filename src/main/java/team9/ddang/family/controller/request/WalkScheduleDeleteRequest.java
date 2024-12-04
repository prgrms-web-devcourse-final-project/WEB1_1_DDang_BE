package team9.ddang.family.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team9.ddang.family.service.request.WalkScheduleDeleteServiceRequest;

import java.util.List;

public record WalkScheduleDeleteRequest(
        @Schema(description = "삭제할 산책 일정 아이디 리스트", example = "[1, 2, 3]")
        @NotNull(message = "삭제할 산책 일정 ID 리스트를 입력해주세요.")
        List<Long> walkScheduleId
) {
    public WalkScheduleDeleteServiceRequest toServiceRequest() {
        return new WalkScheduleDeleteServiceRequest(walkScheduleId);
    }
}
