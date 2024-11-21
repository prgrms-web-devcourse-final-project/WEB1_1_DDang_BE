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
        @NotBlank(message = "이름이 빈 칸입니다.")
        String name,

        @NotNull(message = "생년월일이 필요합니다.")
        LocalDate birthDate,

        @NotNull(message = "성별이 필요합니다.")
        Gender gender,

        FamilyRole familyRole,

        String profileImg
) {

    public JoinServiceRequest toServiceRequest(String email, Provider provider) {
        return new JoinServiceRequest(
                email,
                name,
                birthDate,
                gender,
                familyRole,
                null,
                profileImg,
                IsMatched.TRUE,
                provider,
                Role.ROLE_USER
        );
    }
}
