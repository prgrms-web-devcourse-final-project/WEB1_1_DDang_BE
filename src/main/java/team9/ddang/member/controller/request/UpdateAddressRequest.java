package team9.ddang.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import team9.ddang.member.service.request.UpdateAddressServiceRequest;

@Schema(description = "주소 수정 요청 데이터")
public record UpdateAddressRequest(

        @NotBlank(message = "주소를 입력해주세요")
        @Schema(description = "주소", example = "서울시 강남구")
        String address
) {
    public UpdateAddressServiceRequest toServiceRequest() {
        return new UpdateAddressServiceRequest(address);
    }
}
