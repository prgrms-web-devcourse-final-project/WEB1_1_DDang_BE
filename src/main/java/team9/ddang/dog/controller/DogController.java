package team9.ddang.dog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team9.ddang.dog.controller.request.CreateDogRequest;
import team9.ddang.dog.controller.request.UpdateDogRequest;
import team9.ddang.dog.service.DogService;
import team9.ddang.dog.service.response.CreateDogResponse;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "반려견 등록",
            description = "반려견을 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "반려견 등록 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateDogRequest.class)
                    )
            ),
            parameters = {
                    @Parameter( name = "profileImgFile",
                            description = "Profile Image File",
                            required = false,
                            schema = @Schema(type = "string", format = "binary") ) }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "반려견 등록 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이름 누락",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"이름은 비워둘 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "이름 크기 초과",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"이름은 최대 10자까지 입력 가능합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "품종 누락",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"품종은 비워둘 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "생년월일 이상",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"생년월일은 과거 혹은 현재 날짜여야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "몸무게 최소치",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"몸무게는 최소 1kg 이상이어야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "몸무게 최대치",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"몸무게는 최대 100kg 이하여야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "몸무게 이상",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"몸무게는 소수점 둘째 자리까지만 가능합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "성별 누락",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"성별은 반드시 입력해야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "중성화 여부 누락",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"중성화 여부는 반드시 입력해야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "코멘트 크기 초과",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"코멘트는 최대 30자까지 입력 가능합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "강아지를 이미 소유하고 있는 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"강아지는 한마리만 소유할 수 있습니다.\", \"data\": null }"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<CreateDogResponse> createDog(
            @RequestPart @Valid CreateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        Long memberId = customOAuth2User.getMember().getMemberId();
        CreateDogResponse response = dogService.createDog(request.toServiceRequest(), memberId, profileImgFile);
        return ApiResponse.created(response);
    }

    @GetMapping("/{dogId}")
    @Operation(
            summary = "반려견 조회",
            description = "반려견을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "반려견 조회 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 강아지",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 강아지를 찾을 수 없습니다.\", \"data\": null }"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<GetDogResponse> getMyDog(
            @PathVariable Long dogId) {

        // 서비스 호출
        GetDogResponse response = dogService.getDogByDogId(dogId);

        // ApiResponse로 바로 반환
        return ApiResponse.ok(response);
    }

    @PatchMapping(value = "/{dogId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "반려견 정보 수정",
            description = "반려견 정보를 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "반려견 수정 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateDogRequest.class)
                    )
            ),
            parameters = {
                    @Parameter( name = "profileImgFile",
                            description = "Profile Image File",
                            required = false,
                            schema = @Schema(type = "string", format = "binary") ) }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "반려견 정보 수정 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이름 크기 초과",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"이름은 최대 10자까지 입력 가능합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "생년월일 이상",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"생년월일은 과거 혹은 현재 날짜여야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "몸무게 최소치",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"몸무게는 최소 1kg 이상이어야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "몸무게 최대치",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"몸무게는 최대 100kg 이하여야 합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "몸무게 이상",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"몸무게는 소수점 둘째 자리까지만 가능합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "코멘트 크기 초과",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"코멘트는 최대 30자까지 입력 가능합니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 강아지",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 강아지를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "강아지를 소유하지 않은 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 강아지의 소유자가 아닙니다.\", \"data\": null }"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<Void> updateDog(
            @PathVariable Long dogId,
            @RequestPart @Valid UpdateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.updateDog(request.toServiceRequest(dogId), memberId, profileImgFile);
        return ApiResponse.noContent();
    }

    @DeleteMapping("/{dogId}")
    @Operation(
            summary = "반려견 삭제",
            description = "반려견을 삭제합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "반려견 정보 수정 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 강아지",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 강아지를 찾을 수 없습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "패밀리댕 주인이 아닌 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"패밀리댕 주인만 강아지를 삭제할 수 있습니다.\", \"data\": null }"
                                    ),
                                    @ExampleObject(
                                            name = "강아지를 소유하지 않은 경우",
                                            value = "{ \"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"해당 강아지의 소유자가 아닙니다.\", \"data\": null }"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<Void> deleteDog(
            @PathVariable Long dogId,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.deleteDog(dogId, memberId);
        return ApiResponse.noContent();
    }
}

