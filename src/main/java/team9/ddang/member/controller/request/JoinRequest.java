package team9.ddang.member.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team9.ddang.global.entity.Gender;
import team9.ddang.family.entity.Family;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.entity.Provider;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.entity.Role;
import team9.ddang.member.service.request.JoinServiceRequest;

import java.time.LocalDate;

public record JoinRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        String email,

        @NotNull(message = "Provider를 입력해주세요.")
        Provider provider,

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "생년월일을 입력해주세요.")
        LocalDate birthDate,

        @NotNull(message = "성별을 입력해주세요.")
        Gender gender,

        @NotBlank(message = " 주소를 입력해주세요.")
        String address,

        FamilyRole familyRole,

        String profileImg
) {

    public JoinServiceRequest toServiceRequest() {
        return new JoinServiceRequest(
                email,
                provider,
                name,
                birthDate,
                gender,
                address,
                familyRole,
                profileImg,
                IsMatched.TRUE,
                Role.USER
        );
    }
}
