package team9.ddang.dog.dto;

import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.time.LocalDate;

public record GetDogResponse(
        Long dogId,
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
