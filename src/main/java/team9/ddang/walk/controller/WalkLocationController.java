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
import team9.ddang.walk.controller.request.walk.DecisionWalkRequest;
import team9.ddang.walk.controller.request.walk.ProposalWalkRequest;
import team9.ddang.walk.controller.request.walk.StartWalkRequest;
import team9.ddang.walk.service.WalkLocationService;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;

    @MessageMapping("/api/v1/walk-alone")
    @ExtractEmail
    public void startWalk(SimpMessageHeaderAccessor headerAccessor ,@RequestBody @Valid StartWalkRequest startWalkRequest) {
        walkLocationService.startWalk(AuthenticationContext.getEmail() , startWalkRequest.toService());
    }

    @MessageMapping("/api/v1/proposal")
    @ExtractEmail
    public void proposalWalk(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid ProposalWalkRequest proposalWalkRequest){
        walkLocationService.proposalWalk(AuthenticationContext.getEmail(), proposalWalkRequest.toService());
    }

    @MessageMapping("/api/v1/decision")
    @ExtractEmail
    public void decisionWalk(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid DecisionWalkRequest decisionWalkRequest){
        walkLocationService.decisionWalk(AuthenticationContext.getEmail(), decisionWalkRequest.toService());
    }

    @MessageMapping("/api/v1/walk-with")
    @ExtractEmail
    public void startWalkWith(SimpMessageHeaderAccessor headerAccessor, @RequestBody @Valid StartWalkRequest startWalkRequest){
        walkLocationService.startWalkWith(AuthenticationContext.getEmail() , startWalkRequest.toService());
    }
}
