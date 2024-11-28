package team9.ddang.dog.controller;

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

    @PostMapping
    public ApiResponse<CreateDogResponse> createDog(
            @RequestBody @Valid CreateDogRequest request,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long memberId = customOAuth2User.getMember().getMemberId();
        CreateDogResponse response = dogService.createDog(request.toServiceRequest(), memberId);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<GetDogResponse> getDogById(@PathVariable Long id) {
        GetDogResponse response = dogService.getDogById(id);
        return ApiResponse.ok(response);
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> updateDog(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDogRequest request,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.updateDog(request.toServiceRequest(id), memberId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDog(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.deleteDog(id, memberId);
        return ApiResponse.ok(null);
    }
}

