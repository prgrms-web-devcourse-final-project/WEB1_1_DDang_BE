package team9.ddang.dog.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team9.ddang.dog.controller.request.CreateDogRequest;
import team9.ddang.dog.controller.request.UpdateDogRequest;

import team9.ddang.dog.service.response.CreateDogResponse;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.dog.service.DogService;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;

//import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping("/create")
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
            @RequestBody @Valid CreateDogRequest request,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long memberId = customOAuth2User.getMember().getMemberId();
        CreateDogResponse response = dogService.createDog(request.toServiceRequest(), memberId);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "반려견 조회",
            description = "반려견을 조회합니다."
    )
    public ApiResponse<GetDogResponse> getMyDog(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        // 로그인된 사용자 ID 가져오기
        Long memberId = customOAuth2User.getMember().getMemberId();

        // 서비스 호출
        GetDogResponse response = dogService.getDogByMemberId(memberId);

        // ApiResponse로 바로 반환
        return ApiResponse.ok(response);
    }

    @PatchMapping("/{id}")
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
            @RequestBody @Valid UpdateDogRequest request,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.updateDog(request.toServiceRequest(id), memberId);
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

