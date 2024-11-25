package team9.ddang.dog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.ddang.dog.controller.request.CreateDogRequest;
import team9.ddang.dog.controller.request.UpdateDogRequest;

import team9.ddang.dog.service.response.CreateDogResponse;
import team9.ddang.dog.service.response.GetDogResponse;
import team9.ddang.dog.service.DogService;
import team9.ddang.global.api.ApiResponse;

//import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateDogResponse>> createDog(@RequestBody @Valid CreateDogRequest request) {
        CreateDogResponse response = dogService.createDog(request.toServiceRequest());
        return ResponseEntity.ok(ApiResponse.created(response)); // 생성된 강아지 정보 반환
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetDogResponse>> getDogById(@PathVariable Long id) {
        GetDogResponse response = dogService.getDogById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateDog(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDogRequest request
    ) {
        dogService.updateDog(request.toServiceRequest(id));
        return ResponseEntity.ok(ApiResponse.ok(null)); // 성공 시 메시지만 반환
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDog(@PathVariable Long id) {
        dogService.deleteDog(id);
        return ResponseEntity.ok(ApiResponse.ok(null)); // 성공 메시지만 반환
    }
}

