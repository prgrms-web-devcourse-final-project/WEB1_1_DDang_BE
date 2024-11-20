package team9.ddang.walk.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.walk.controller.request.CompleteWalkRequest;
import team9.ddang.walk.service.WalkService;
import team9.ddang.walk.service.response.CompleteWalkAloneResponse;

@RestController
@RequestMapping("/api/v1/walk")
@RequiredArgsConstructor
public class WalkController {

    private final WalkService walkService;

    @PostMapping("/complete")
    public ApiResponse<CompleteWalkAloneResponse> completeWalk(@RequestBody @Valid CompleteWalkRequest request){ // TODO : 멤버 인증 정보 추가 예정
        return ApiResponse.ok(walkService.completeWalk(1L, request.toServiceRequest()));
    }

}
