package team9.ddang.dog.service;
//깃허브 데스크탑
//테스트

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.exception.DogExceptionMessage;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.dog.service.request.CreateDogServiceRequest;
import team9.ddang.dog.service.response.CreateDogResponse;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.dog.service.request.UpdateDogServiceRequest;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.global.entity.IsDeleted;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.util.List;

//import team9.ddang.family.entity.Family;
//import team9.ddang.family.repository.FamilyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DogService {

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;
    private final MemberDogRepository memberDogRepository; // MemberDog 저장
    private final FamilyRepository familyRepository;

    public CreateDogResponse createDog(CreateDogServiceRequest request, Long memberId) {

        //  memberId로 Member 객체 조회
        Member member = findMemberByIdOrThrowException(memberId);

        // TODO : 지금은 강아지 한마리만 소유 가능
        List<MemberDog> memberDogs = memberDogRepository.findAllByMember(member);
        if(!memberDogs.isEmpty()) {
            throw new IllegalArgumentException(DogExceptionMessage.DOG_ONLY_ONE.getText());
        }

        if(member.getFamily() != null && memberId.equals(member.getFamily().getMember().getMemberId())){
            throw new IllegalArgumentException(DogExceptionMessage.ONLY_FAMILY_OWNER_CREATE.getText());
        }


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

        // 4. Dog 저장
        dogRepository.save(dog);

        // 5. MemberDog 엔티티 생성 및 저장
        MemberDog memberDog = MemberDog.builder()
                .member(member)
                .dog(dog)
                .build();
        memberDogRepository.save(memberDog);

        // 6. CreateDogResponse 반환
        return new CreateDogResponse(
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


    public GetDogResponse getDogByMemberId(Long memberId) {
        // 1. MemberDog 조회
        MemberDog memberDog = memberDogRepository.findOneByMemberIdAndNotDeleted(memberId)
                .orElseThrow(() -> new IllegalArgumentException("강아지를 소유하고 있지 않습니다."));

        // 2. 강아지 정보 반환
        Dog dog = memberDog.getDog();
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

    public void updateDog(UpdateDogServiceRequest request, Long memberId) {

        // 1. 소유권 검증
        memberDogRepository.findByDogIdAndMemberId(request.dogId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강아지의 소유자가 아닙니다."));

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

    public void deleteDog(Long dogId, Long memberId) {
        // 1. 소유권 검증
        MemberDog memberDog = memberDogRepository.findByDogIdAndMemberId(dogId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강아지의 소유자가 아닙니다."));

        // 2. Dog 엔티티 가져오기
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with id: " + dogId));

        // 3. MemberDog 소프트 삭제
        memberDogRepository.softDeleteByDogIdAndMemberId(dogId, memberId);

        // 4. Dog 소프트 삭제
        dogRepository.softDeleteById(dogId);
    }


    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.MEMBER_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.MEMBER_NOT_FOUND.getText());
                });
    }

    private Dog findDogByIdOrThrowException(Long id) {
        return dogRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, FamilyExceptionMessage.DOG_NOT_FOUND);
                    return new IllegalArgumentException(FamilyExceptionMessage.DOG_NOT_FOUND.getText());
                });
    }


}


