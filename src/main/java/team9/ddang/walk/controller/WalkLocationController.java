package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import team9.ddang.global.aop.AuthenticationContext;
import team9.ddang.global.aop.ExtractEmail;
import team9.ddang.global.exception.AuthenticationException;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.walk.controller.request.DecisionWalkRequest;
import team9.ddang.walk.controller.request.ProposalWalkRequest;
import team9.ddang.walk.controller.request.StartWalkRequest;
import team9.ddang.walk.service.WalkLocationService;

import static team9.ddang.walk.exception.WalkExceptionMessage.TOKEN_DO_NOT_EXTRACT_EMAIL;
import static team9.ddang.walk.exception.WalkExceptionMessage.TOKEN_NOT_FOUND;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;
    private final JwtService jwtService;

    @MessageMapping("/api/v1/walk-alone")
    @ExtractEmail
    public void startWalk(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid StartWalkRequest startWalkRequest) {
        String email = AuthenticationContext.getEmail();

        walkLocationService.startWalk(email , startWalkRequest.toService());
    }

    @MessageMapping("/api/v1/proposal")
    @ExtractEmail
    public void proposalWalk(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid ProposalWalkRequest proposalWalkRequest){
        String token = jwtService.extractAccessToken(headerAccessor).orElseThrow(() -> new AuthenticationException(TOKEN_NOT_FOUND));
        String email = jwtService.extractEmail(token).orElseThrow(() -> new AuthenticationException(TOKEN_DO_NOT_EXTRACT_EMAIL));

        walkLocationService.proposalWalk(email, proposalWalkRequest.toService());
    }

    @MessageMapping("/api/v1/decision")
    @ExtractEmail
    public void decisionWalk(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid DecisionWalkRequest decisionWalkRequest){
        String token = jwtService.extractAccessToken(headerAccessor).orElseThrow(() -> new AuthenticationException(TOKEN_NOT_FOUND));
        String email = jwtService.extractEmail(token).orElseThrow(() -> new AuthenticationException(TOKEN_DO_NOT_EXTRACT_EMAIL));

        walkLocationService.decisionWalk(email, decisionWalkRequest.toService());
    }

    @MessageMapping("/api/v1/walk-with")
    @ExtractEmail
    public void startWalkWith(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid StartWalkRequest startWalkRequest){
        String token = jwtService.extractAccessToken(headerAccessor).orElseThrow(() -> new AuthenticationException(TOKEN_NOT_FOUND));
        String email = jwtService.extractEmail(token).orElseThrow(() -> new AuthenticationException(TOKEN_DO_NOT_EXTRACT_EMAIL));

        walkLocationService.startWalkWith(email , startWalkRequest.toService());
    }
}
