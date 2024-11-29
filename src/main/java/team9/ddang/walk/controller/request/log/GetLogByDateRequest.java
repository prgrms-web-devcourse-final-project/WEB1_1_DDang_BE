package team9.ddang.walk.controller.request.log;

import jakarta.validation.constraints.NotNull;
import team9.ddang.walk.service.request.log.GetLogByDateServiceRequest;

import java.time.LocalDate;

public record GetLogByDateRequest(
        @NotNull(message = "날짜 값은 필수 값 입니다.")
        LocalDate date
) {
        public GetLogByDateServiceRequest toService() {
                return new GetLogByDateServiceRequest(date);
        }
}
