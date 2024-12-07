package team9.ddang.dog.service;
//깃허브 데스크탑
//테스트

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team9.ddang.dog.entity.Dog;
import team9.ddang.dog.entity.MemberDog;
import team9.ddang.dog.exception.DogExceptionMessage;
import team9.ddang.dog.repository.DogRepository;
import team9.ddang.dog.repository.MemberDogRepository;
import team9.ddang.dog.service.request.CreateDogServiceRequest;
import team9.ddang.dog.service.request.UpdateDogServiceRequest;
import team9.ddang.dog.service.response.CreateDogResponse;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.family.entity.Family;
import team9.ddang.family.exception.FamilyExceptionMessage;
import team9.ddang.family.repository.FamilyRepository;
import team9.ddang.family.repository.WalkScheduleRepository;
import team9.ddang.global.service.S3Service;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DogService {

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkScheduleRepository walkScheduleRepository;
    private final FamilyRepository familyRepository;
    private final S3Service s3Service;

    private final static String DOG_PROFILE_DIR = "dog";

    public CreateDogResponse createDog(CreateDogServiceRequest request, Long memberId, MultipartFile profileImgFile) throws IOException {

        //  memberId로 Member 객체 조회
        Member member = findMemberByIdOrThrowException(memberId);
        String profileImg = s3Service.upload(profileImgFile, DOG_PROFILE_DIR);

        // TODO : 지금은 강아지 한마리만 소유 가능
        List<MemberDog> memberDogs = memberDogRepository.findAllByMember(member);
        if(!memberDogs.isEmpty()) {
            throw new IllegalArgumentException(DogExceptionMessage.DOG_ONLY_ONE.getText());
        }

        if(member.getFamily() != null && !memberId.equals(member.getFamily().getMember().getMemberId())){
            throw new IllegalArgumentException(DogExceptionMessage.ONLY_FAMILY_OWNER_CREATE.getText());
        }

        if(member.getFamily() == null) {
            Family family = Family.builder()
                    .member(member)
                    .familyName("")
                    .build();

            family = familyRepository.save(family);

            member.updateFamily(family);
        }


        // Dog 엔티티 생성 및 저장
        Dog dog = Dog.builder()
                .name(request.name())
                .breed(request.breed())
                .birthDate(request.birthDate())
                .weight(request.weight())
                .gender(request.gender())
                .isNeutered(request.isNeutered())
                .profileImg(profileImg)
                .family(member.getFamily())
                .comment(request.comment())
                .build();

        // 4. Dog 저장
        dogRepository.save(dog);

        // 5. MemberDog 엔티티 생성 및 저장
        List<Member> members = memberRepository.findAllByFamilyId(member.getFamily().getFamilyId());
        List<MemberDog> memberDog = members.stream()
                .map(familyMember -> MemberDog.builder()
                        .member(familyMember)
                        .dog(dog)
                        .build())
                .collect(Collectors.toList());

        memberDogRepository.saveAll(memberDog);

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


    public GetDogResponse getDogByDogId(Long dogId) {

        Dog dog = findDogByIdOrThrowException(dogId);
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

    public void updateDog(UpdateDogServiceRequest request, Long memberId, MultipartFile profileImgFile) throws IOException {

        // 1. 소유권 검증
        memberDogRepository.findByDogIdAndMemberId(request.dogId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException(DogExceptionMessage.MEMBER_NOT_HAVE_DOG.getText()));

        // 기존 데이터 조회
        Dog dog = findDogByIdOrThrowException(request.dogId());

        // 필드별 업데이트 로직
        if (request.name() != null) dog.updateName(request.name());
        if (request.breed() != null) dog.updateBreed(request.breed());
        if (request.birthDate() != null) dog.updateBirthDate(request.birthDate());
        if (request.weight() != null) dog.updateWeight(request.weight());
        if (request.gender() != null) dog.updateGender(request.gender());
        if (profileImgFile != null) {
            String profileImg = s3Service.upload(profileImgFile, DOG_PROFILE_DIR);
            dog.updateProfileImg(profileImg);
        }
        if (request.isNeutered() != null) dog.updateIsNeutered(request.isNeutered());
        if (request.comment() != null) dog.updateComment(request.comment());
    }

    public void deleteDog(Long dogId, Long memberId) {

        Member member = findMemberByIdOrThrowException(memberId);

        findDogByIdOrThrowException(dogId);

        memberDogRepository.findByDogIdAndMemberId(dogId, memberId)
                .orElseThrow(() -> new IllegalArgumentException(DogExceptionMessage.MEMBER_NOT_HAVE_DOG.getText()));

        if(member.getFamily() != null && !memberId.equals(member.getFamily().getMember().getMemberId())){
            throw new IllegalArgumentException(DogExceptionMessage.ONLY_FAMILY_OWNER_DELETE.getText());
        }

        // 3. MemberDog 소프트 삭제
        memberDogRepository.softDeleteByDogId(dogId);

        // 4. Dog 소프트 삭제
        dogRepository.softDeleteById(dogId);

        walkScheduleRepository.softDeleteByDogId(dogId);

        // TODO 산책 내역 삭제하기
    }

    public GetDogResponse getDogByMemberId(Long memberId) {
        // 1. MemberDog 조회
        MemberDog memberDog = memberDogRepository.findOneByMemberIdAndNotDeleted(memberId)
                .orElseThrow(() -> new IllegalArgumentException(DogExceptionMessage.MEMBER_NOT_HAVE_DOG.getText()));

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


