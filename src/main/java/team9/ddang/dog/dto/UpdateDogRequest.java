package team9.ddang.dog.dto;

import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.time.LocalDate;
import java.util.Optional;

public record UpdateDogRequest(
        Optional<String> name,
        Optional<String> breed,
        Optional<LocalDate> birthDate,
        Optional<Integer> weight,
        Optional<Gender> gender,
        Optional<String> profileImg,
        Optional<IsNeutered> isNeutered,
        Optional<Long> familyId,
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


