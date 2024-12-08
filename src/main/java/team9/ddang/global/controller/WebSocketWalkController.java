package team9.ddang.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                            responseCode = "200",
                            description = "혼자 산책 시작",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "올바르지 않은 요청의 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "위도값이 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"위도는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "위도값이 90보다 큰 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"위도는 90.0 이하여야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "위도값이 -90보다 작은 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"위도는 -90.0 이상이여야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "경도값이 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"경도는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "경도값이 180보다 큰 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"경도는 180.0 이하여야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "경도값이 -180보다 작은 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"경도는 -180.0 이상 이어야 합니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 실패 예시",
                                                    value = "{ \"code\": 4001, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "토큰이 없는 경우",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"토큰을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 추출 실패",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken에서 email을 추출할 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            value = "{ \"code\": 5000, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류\", \"data\": null }"
                                    )
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
                            responseCode = "200",
                            description = "산책 제안 처리",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "올바르지 않은 요청의 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "상대방 이메일이 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"상대 이메일은 입력해주셔야 해요\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 형식이 올바르지 않은 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"올바른 이메일 형식을 입력해주세요.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이미 다른 사람에게 제안한 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"이미 다른 견주분에게 산책을 제안을 하신 상태 입니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 실패 예시",
                                                    value = "{ \"code\": 4001, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "토큰이 없는 경우",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"토큰을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 추출 실패",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken에서 email을 추출할 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            value = "{ \"code\": 5000, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류\", \"data\": null }"
                                    )
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
                            responseCode = "200",
                            description = "산책 결정 처리",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "올바르지 않은 요청의 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "상대방 이메일이 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"상대 이메일은 입력해주셔야 해요\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 형식이 올바르지 않은 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"올바른 이메일 형식을 입력해주세요.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "제안이 사라진 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"제안을 취소했거나 이미 강번따를 진행 중인 유저 입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일이 실제 보낸 사람 이메일과 일치하지 않는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"제안을 한 유저와 받은 유저가 일치하지 않습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 실패 예시",
                                                    value = "{ \"code\": 4001, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "토큰이 없는 경우",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"토큰을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 추출 실패",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken에서 email을 추출할 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            value = "{ \"code\": 5000, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류\", \"data\": null }"
                                    )
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
                            responseCode = "200",
                            description = "혼자 산책 시작",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "올바르지 않은 요청의 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "위도값이 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"위도는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "위도값이 90보다 큰 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"위도는 90.0 이하여야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "위도값이 -90보다 작은 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"위도는 -90.0 이상이여야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "경도값이 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"경도는 필수입니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "경도값이 180보다 큰 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"경도는 180.0 이하여야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "경도값이 -180보다 작은 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"경도는 -180.0 이상 이어야 합니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "상대방 이메일을 찾을 수 없는 경우",
                                                    value = "{ \"code\": 4000, \"status\": \"BAD_REQUEST\", \"message\": \"상대 이메일 정보가 존재하지 않습니다.\", \"data\": null }"
                                            )

                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증 실패 예시",
                                                    value = "{ \"code\": 4001, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "토큰이 없는 경우",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"토큰을 찾을 수 없습니다.\", \"data\": null }"
                                            ),
                                            @ExampleObject(
                                                    name = "이메일 추출 실패",
                                                    value = "{ \"code\": 4002, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken에서 email을 추출할 수 없습니다.\", \"data\": null }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 (감지하지 못한 서버 에러)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WebSocketErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            value = "{ \"code\": 5000, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류\", \"data\": null }"
                                    )
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
