package team9.ddang.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import team9.ddang.global.entity.Gender;
import team9.ddang.member.entity.FamilyRole;
import team9.ddang.member.service.request.UpdateServiceRequest;

@Schema(description = "회원 정보 수정 요청 데이터")
public record UpdateRequest(

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "성별을 입력해주세요.")
        Gender gender,

        @NotNull(message = "가족 역할을 입력해주세요.")
        FamilyRole familyRole,

        @NotBlank(message = "프로필 이미지를 입력해주세요.")
        String profileImg
) {
    public UpdateServiceRequest toServiceRequest() {
        return new UpdateServiceRequest(
                name,
                gender,
                familyRole,
                profileImg
        );
    }
}
