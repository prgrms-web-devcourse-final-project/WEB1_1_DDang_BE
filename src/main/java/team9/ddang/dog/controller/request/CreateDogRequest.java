package team9.ddang.dog.controller.request;

import team9.ddang.dog.service.request.CreateDogServiceRequest;
import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.time.LocalDate;

public record CreateDogRequest(
        String name,
        String breed,
        LocalDate birthDate,
        Integer weight,
        Gender gender,
        String profileImg,
        IsNeutered isNeutered,
        Long familyId,
        String comment
) {
    public CreateDogServiceRequest toServiceRequest() {
        return new CreateDogServiceRequest(
                name,
                breed,
                birthDate,
                weight,
                gender,
                profileImg,
                isNeutered,
                familyId,
                comment
        );
    }
}

