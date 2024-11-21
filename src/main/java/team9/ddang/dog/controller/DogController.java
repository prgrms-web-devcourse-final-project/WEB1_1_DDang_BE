package team9.ddang.dog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team9.ddang.dog.dto.CreateDogRequest;
import team9.ddang.dog.dto.UpdateDogRequest;
import team9.ddang.dog.service.DogService;

@RestController
@RequestMapping("/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping
    public ResponseEntity<Void> createDog(@RequestBody CreateDogRequest request) {
        // Service 계층 호출
        dogService.createDog(request.toServiceRequest());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public <GetDogResponse> ResponseEntity<GetDogResponse> getDogById(@PathVariable Long id) {
        GetDogResponse response = (GetDogResponse) dogService.getDogById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateDog(
            @PathVariable Long id,
            @RequestBody UpdateDogRequest request
    ) {
        dogService.updateDog(request.toServiceRequest(id));
        return ResponseEntity.noContent().build(); // 업데이트 성공 시 204 반환
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        dogService.deleteDog(id);
        return ResponseEntity.noContent().build(); // 삭제 성공 시 204 반환
    }

}
