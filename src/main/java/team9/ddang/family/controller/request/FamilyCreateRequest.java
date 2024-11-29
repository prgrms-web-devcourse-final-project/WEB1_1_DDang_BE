package team9.ddang.family.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "가족 생성 요청 데이터")
public record FamilyCreateRequest(
        @NotBlank(message = "가족 이름은 필수입니다.")
        @Schema(description = "가족 이름", example = "행복한 가족")
        String familyName
) {
}
