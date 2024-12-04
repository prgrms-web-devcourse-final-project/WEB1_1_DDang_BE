package team9.ddang.dog.service.request;

import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.global.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDogServiceRequest(
        String name,
        String breed,
        LocalDate birthDate,
        BigDecimal weight,
        Gender gender,
        IsNeutered isNeutered,
        String comment
) {}

