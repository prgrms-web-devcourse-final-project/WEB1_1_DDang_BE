package team9.ddang.member.controller.request;

import jakarta.validation.constraints.NotNull;
import team9.ddang.member.service.request.ReportServiceRequest;

public record ReportRequest(
        @NotNull(message = "신고 대상 사용자의 ID는 필수입니다.")
        Long receiverId,

        String reason
) {
    public ReportServiceRequest toServiceRequest(){
        return new ReportServiceRequest(receiverId, reason);
    }
}
