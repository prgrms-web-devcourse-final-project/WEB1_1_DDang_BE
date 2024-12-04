package team9.ddang.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.global.controller.response.WebSocketErrorResponse;
import team9.ddang.walk.controller.request.walk.DecisionWalkRequest;
import team9.ddang.walk.controller.request.walk.ProposalWalkRequest;
import team9.ddang.walk.controller.request.walk.StartWalkRequest;
import team9.ddang.walk.service.response.walk.DecisionWalkResponse;
import team9.ddang.walk.service.response.walk.MemberNearbyResponse;
import team9.ddang.walk.service.response.walk.ProposalWalkResponse;
import team9.ddang.walk.service.response.walk.WalkWithResponse;

@Tag(name = "WebSocket Walk API", description = "WebSocket을 통한 산책 관련 명세")
@RestController
public class WebSocketWalkController {

    @Operation(
            summary = "혼자 산책 기능",
            description = "WebSocket을 통해 자신의 좌표 데이터를 전송합니다. 메시지를 전송하려면 '/pub/api/v1/walk-alone' 경로로 JSON 데이터를 전송하세요. 응답 값은 근처 유저 정보 입니다. 없을 시 응답 없음.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "좌표 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StartWalkRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    )

            }
    )
    @GetMapping("/api/v1/walk-alone")
    public WebSocketResponse<MemberNearbyResponse> startWalk() {
        MemberNearbyResponse response = null;
        return WebSocketResponse.ok(response);
    }

    @Operation(
            summary = "강번따 제안 기능",
            description = "WebSocket을 통해 자신의 데이터 및 코멘트를 전송합니다. 메시지를 전송하려면 '/pub/api/v1/proposal' 경로로 JSON 데이터를 전송하세요.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "강번따 제안 코멘트 및 상대방 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProposalWalkRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/api/v1/proposal")
    public WebSocketResponse<ProposalWalkResponse> proposalWalk(){
        ProposalWalkResponse response = null;
        return WebSocketResponse.ok(response);
    }

    @Operation(
            summary = "강번따 제안 수락 기능",
            description = "WebSocket을 통해 강번따 제안 수락 여부를 전송합니다. 메시지를 전송하려면 '/pub/api/v1/decision' 경로로 JSON 데이터를 전송하세요.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "상대방 이메일 및 수락여부 ACCEPT, DENY 로 보내주세요. 응답 값도 동일하게 ACCEPT, DENY 로 처리 됩니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DecisionWalkRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/api/v1/decision")
    public WebSocketResponse<DecisionWalkResponse> decisionWalk(){
        DecisionWalkResponse response = null;
        return WebSocketResponse.ok(response);
    }

    @Operation(
            summary = "같이 산책 기능",
            description = "WebSocket을 통해 자신의 좌표 데이터를 전송합니다. 메시지를 전송하려면 '/pub/api/v1/walk-with' 경로로 JSON 데이터를 전송하세요.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "좌표 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StartWalkRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (유효성 검사 실패 또는 기타 클라이언트 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class)
                            )
                    )

            }
    )
    @GetMapping("/api/v1/walk-with")
    public WebSocketResponse<WalkWithResponse> startWalkWith() {
        WalkWithResponse response = null;
        return WebSocketResponse.ok(response);
    }

    @Operation(
            summary = "멤버 구독 url",
            description = " 해당 url에 웹소켓 연결 하셔야 위에 response 값들이 전달 됩니다."
    )
    @PostMapping("/sub/walk/{email}")
    public void subMemberEmail(){

    }
}
