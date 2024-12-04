package team9.ddang.family.service.request;

import java.util.List;

public record WalkScheduleDeleteServiceRequest(
        List<Long> walkScheduleId
) {
}
