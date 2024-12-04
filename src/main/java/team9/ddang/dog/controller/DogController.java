package team9.ddang.dog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
            )
    )
    public ApiResponse<CreateDogResponse> createDog(
            @RequestPart @Valid CreateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        Long memberId = customOAuth2User.getMember().getMemberId();
        CreateDogResponse response = dogService.createDog(request.toServiceRequest(), memberId, profileImgFile);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "반려견 조회",
            description = "반려견을 조회합니다."
    )
    public ApiResponse<GetDogResponse> getMyDog(
            @PathVariable Long id) {

        // 서비스 호출
        GetDogResponse response = dogService.getDogByDogId(id);

        // ApiResponse로 바로 반환
        return ApiResponse.ok(response);
    }

    @PatchMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
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
            )
    )
    public ApiResponse<Void> updateDog(
            @PathVariable Long id,
            @RequestPart @Valid UpdateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.updateDog(request.toServiceRequest(id), memberId, profileImgFile);
        return ApiResponse.noContent();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "반려견 삭제",
            description = "반려견을 삭제합니다."
    )
    public ApiResponse<Void> deleteDog(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.deleteDog(id, memberId);
        return ApiResponse.noContent();
    }
}

