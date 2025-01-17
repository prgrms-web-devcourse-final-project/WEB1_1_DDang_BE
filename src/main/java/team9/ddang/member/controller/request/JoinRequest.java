package team9.ddang.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import team9.ddang.global.entity.Gender;
import team9.ddang.family.entity.Family;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Role;
import team9.ddang.member.service.request.JoinServiceRequest;

import java.time.LocalDate;

@Schema(description = "회원가입 요청 데이터")
public record JoinRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일 형식을 입력해주세요.")
        @Schema(description = "이메일", example = "test@naver.com")
        String email,

        @NotNull(message = "Provider를 입력해주세요.")
        @Schema(description = "Provider", example = "NAVER")
        Provider provider,

        @NotBlank(message = "이름을 입력해주세요.")
        @Schema(description = "이름", example = "홍길동")
        String name,

        @NotNull(message = "성별을 입력해주세요.")
        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @NotBlank(message = " 주소를 입력해주세요.")
        @Schema(description = "주소", example = "서울시 강남구")
        String address,

        @NotNull(message = "가족 역할을 입력해주세요.")
        @Schema(description = "가족 역할", example = "FATHER")
        FamilyRole familyRole,

        @NotBlank(message = "프로필 이미지를 입력해주세요.")
        @Schema(description = "프로필 이미지", example = "https://example.com/profile.jpg")
        String profileImg
) {

    public JoinServiceRequest toServiceRequest() {
        return new JoinServiceRequest(
                email,
                provider,
                name,
                gender,
                address,
                familyRole,
                profileImg,
                IsMatched.TRUE,
                Role.USER
        );
    }
}
