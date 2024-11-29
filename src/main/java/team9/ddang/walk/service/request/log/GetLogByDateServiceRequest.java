package team9.ddang.walk.service.request.log;

import java.time.LocalDate;

public record GetLogByDateServiceRequest(
        LocalDate date
) {
}
