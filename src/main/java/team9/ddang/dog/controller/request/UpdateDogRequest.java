package team9.ddang.dog.controller.request;

import jakarta.validation.constraints.*;
import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.dog.service.request.UpdateDogServiceRequest;
import team9.ddang.global.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDogRequest(
        @Size(max = 100, message = "이름은 최대 100자까지 입력 가능합니다.")
        String name,

        @Size(max = 100, message = "품종은 최대 100자까지 입력 가능합니다.")
        String breed,

        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        LocalDate birthDate,

        @DecimalMin(value = "1.00", message = "몸무게는 최소 1kg 이상이어야 합니다.")
        @DecimalMax(value = "100.00", message = "몸무게는 최대 100kg 이하여야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "몸무게는 소수점 둘째 자리까지만 가능합니다.")
        BigDecimal weight,

        Gender gender,

        IsNeutered isNeutered,

        @Size(max = 30, message = "코멘트는 최대 30자까지 입력 가능합니다.")
        String comment
) {
    public UpdateDogServiceRequest toServiceRequest(Long dogId) {
        return new UpdateDogServiceRequest(
                dogId,
                name,
                breed,
                birthDate,
                weight,
                gender,
                isNeutered,
                comment
        );
    }
}


