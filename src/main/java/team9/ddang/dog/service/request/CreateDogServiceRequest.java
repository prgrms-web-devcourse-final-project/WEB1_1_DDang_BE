package team9.ddang.dog.service.request;

import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.time.LocalDate;

public record CreateDogServiceRequest(
        String name,
        String breed,
        LocalDate birthDate,
        Integer weight,
        Gender gender,
        String profileImg,
        IsNeutered isNeutered,
        Long familyId,
        String comment
) {}
