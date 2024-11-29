package team9.ddang.member.service.request;

public record ReportServiceRequest(
        Long receiverId,
        String reason
) {
}
