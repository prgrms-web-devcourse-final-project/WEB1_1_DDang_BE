package team9.ddang.dog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.dto.CreateDogServiceRequest;
import team9.ddang.dog.dto.GetDogResponse;
import team9.ddang.dog.dto.UpdateDogServiceRequest;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.DogRepository;
//import team9.ddang.family.entity.Family;
//import team9.ddang.family.repository.FamilyRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DogService {

    private final DogRepository dogRepository;
    //private final FamilyRepository familyRepository;

    public void createDog(CreateDogServiceRequest request) {
        // Family 엔티티 검증
       /* Family family = familyRepository.findById(request.familyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid family ID"));*/

        // Dog 엔티티 생성 및 저장
        Dog dog = Dog.builder()
                .name(request.name())
                .breed(request.breed())
                .birthDate(request.birthDate())
                .weight(request.weight())
                .gender(request.gender())
                .isNeutered(request.isNeutered())
                .profileImg(request.profileImg())
                .family(null)
                .comment(request.comment())
                .build();

        dogRepository.save(dog);
    }


    public GetDogResponse getDogById(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with id: " + dogId));

        return new GetDogResponse(
                dog.getDogId(),
                dog.getName(),
                dog.getBreed(),
                dog.getBirthDate(),
                dog.getWeight(),
                dog.getGender(),
                dog.getProfileImg(),
                dog.getIsNeutered(),
                dog.getFamily() != null ? dog.getFamily().getFamilyId() : null,
                dog.getComment()
        );
    }

    public void updateDog(UpdateDogServiceRequest request) {
        // 기존 데이터 조회
        Dog dog = dogRepository.findById(request.dogId())
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with id: " + request.dogId()));

        // 필드별 업데이트 로직
        if (request.name() != null) dog.updateName(request.name());
        if (request.breed() != null) dog.updateBreed(request.breed());
        if (request.birthDate() != null) dog.updateBirthDate(request.birthDate());
        if (request.weight() != null) dog.updateWeight(request.weight());
        if (request.gender() != null) dog.updateGender(request.gender());
        if (request.profileImg() != null) dog.updateProfileImg(request.profileImg());
        if (request.isNeutered() != null) dog.updateIsNeutered(request.isNeutered());
        if (request.familyId() != null) {
            /*Family family = familyRepository.findById(request.familyId())
                    .orElseThrow(() -> new IllegalArgumentException("Family not found with id: " + request.familyId()));*/
            dog.updateFamily(null);
        }
        if (request.comment() != null) dog.updateComment(request.comment());
    }

    public void deleteDog(Long dogId) {
        if (!dogRepository.existsById(dogId)) {
            throw new IllegalArgumentException("Dog not found with id: " + dogId);
        }
        dogRepository.deleteById(dogId);
    }
}


