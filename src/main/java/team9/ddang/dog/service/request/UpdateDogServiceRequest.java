package team9.ddang.dog.service.request;

import team9.ddang.global.entity.Gender;
import team9.ddang.dog.entity.IsNeutered;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDogServiceRequest(
        Long dogId,
        String name,
        String breed,
        LocalDate birthDate,
        BigDecimal weight,
        Gender gender,
        String profileImg,
        IsNeutered isNeutered,
        String comment
) {}

