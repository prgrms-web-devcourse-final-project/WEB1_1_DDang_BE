package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.chat.controller.request.ChatRoomCreateRequest;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.walk.controller.request.CompleteWalkRequest;
import team9.ddang.walk.service.WalkService;
import team9.ddang.walk.service.response.CompleteWalkAloneResponse;

@RestController
@RequestMapping("/api/v1/walk")
@RequiredArgsConstructor
@Tag(name = "Walk API", description = "산책 API")
public class WalkController {

    private final WalkService walkService;

    @Operation(
            summary = "산책 완료",
            description = """
                    산책을 완료해 DB에 저장하고 관련한 소모 칼로리와 위도, 경도를 반환합니다.
                    요청 본문에 산책 시간 및 거리를 포함해야 합니다.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "산책 완료 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompleteWalkRequest.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "산책 완료 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CompleteWalkAloneResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류"
                    )
            }
    )
    @PostMapping("/complete")
    public ApiResponse<CompleteWalkAloneResponse> completeWalk(@RequestBody @Valid CompleteWalkRequest request){

        return ApiResponse.ok(walkService.completeWalk(4L, request.toServiceRequest()));
    }

    // TODO : 멤버 인증 정보 추가 예정

}
