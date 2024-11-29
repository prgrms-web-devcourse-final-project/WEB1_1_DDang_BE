package team9.ddang.dog.controller.request;

import jakarta.validation.constraints.*;
import team9.ddang.dog.service.request.UpdateDogServiceRequest;
import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record UpdateDogRequest(
        @NotBlank(message = "이름은 비워둘 수 없습니다.")
        @Size(max = 100, message = "이름은 최대 100자까지 입력 가능합니다.")
        String name,

        @NotBlank(message = "품종은 비워둘 수 없습니다.")
        @Size(max = 100, message = "품종은 최대 100자까지 입력 가능합니다.")
        String breed,

        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        LocalDate birthDate,

        @DecimalMin(value = "1.00", message = "몸무게는 최소 1kg 이상이어야 합니다.")
        @DecimalMax(value = "100.00", message = "몸무게는 최대 100kg 이하여야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "몸무게는 소수점 둘째 자리까지만 가능합니다.")
        BigDecimal weight,

        @NotNull(message = "성별은 반드시 입력해야 합니다.")
        Gender gender,

        String profileImg,

        @NotNull(message = "중성화 여부는 반드시 입력해야 합니다.")
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
                profileImg,
                isNeutered,
                comment
        );
    }
}


