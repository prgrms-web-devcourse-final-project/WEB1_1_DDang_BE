package team9.ddang.walk.service.response;

import team9.ddang.dog.entity.Dog;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.Member;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ProposalWalkResponse(
        Long dogId,
        String dogName,
        String dogBreed,
        String dogProfileImg,
        String comment,
        Gender memberGender,
        long age
) {
    public static ProposalWalkResponse of(Dog dog, Member member, String comment){
        if(comment == null){
            // TODO : dog 엔티티 변경 시 default comment 대체 예정
        }

        return new ProposalWalkResponse(dog.getDogId(), dog.getName(), dog.getBreed(), dog.getProfileImg(), comment, member.getGender(),
                ChronoUnit.YEARS.between(member.getBirthDate(), LocalDate.now())+1);
    }
}
