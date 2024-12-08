package team9.ddang.family.service.response;

import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.IsNeutered;
import team9.ddang.global.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FamilyDogResponse(
        Long dogId,
        String name,
        String breed,
        LocalDate birthDate,
        BigDecimal weight,
        Gender gender,
        String profileImg,
        IsNeutered isNeutered,
        Long familyId,
        String comment
) {
    public FamilyDogResponse(Dog dog){
        this(
                dog.getDogId(),
                dog.getName(),
                dog.getBreed(),
                dog.getBirthDate(),
                dog.getWeight(),
                dog.getGender(),
                dog.getProfileImg(),
                dog.getIsNeutered(),
                dog.getFamily().getFamilyId(),
                dog.getComment()
        );
    }
}
