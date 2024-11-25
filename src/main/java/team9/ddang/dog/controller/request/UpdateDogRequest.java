package team9.ddang.dog.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import team9.ddang.dog.service.request.UpdateDogServiceRequest;
import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.time.LocalDate;
import java.util.Optional;

public record UpdateDogRequest(
        @Size(max = 100, message = "이름은 최대 100자까지 입력 가능합니다.")
        Optional<String> name,

        @Size(max = 100, message = "품종은 최대 100자까지 입력 가능합니다.")
        Optional<String> breed,

        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        Optional<LocalDate> birthDate,

        @Min(value = 1, message = "몸무게는 최소 1kg 이상이어야 합니다.")
        @Max(value = 100, message = "몸무게는 최대 100kg 이하여야 합니다.")
        Optional<Integer> weight,

        Optional<Gender> gender,

        Optional<String> profileImg,

        Optional<IsNeutered> isNeutered,

        Optional<Long> familyId,

        @Size(max = 30, message = "코멘트는 최대 30자까지 입력 가능합니다.")
        Optional<String> comment
) {
    public UpdateDogServiceRequest toServiceRequest(Long dogId) {
        return new UpdateDogServiceRequest(
                dogId,
                name.orElse(null),
                breed.orElse(null),
                birthDate.orElse(null),
                weight.orElse(null),
                gender.orElse(null),
                profileImg.orElse(null),
                isNeutered.orElse(null),
                familyId.orElse(null),
                comment.orElse(null)
        );
    }
}


