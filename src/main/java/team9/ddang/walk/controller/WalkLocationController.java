package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import team9.ddang.walk.controller.request.walk.DecisionWalkRequest;
import team9.ddang.walk.controller.request.walk.ProposalWalkRequest;
import team9.ddang.walk.controller.request.walk.StartWalkRequest;
import team9.ddang.walk.service.WalkLocationService;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;

    @MessageMapping("/api/v1/walk-alone")
    public void startWalk(Principal principal , @Payload @Valid StartWalkRequest startWalkRequest) {
        walkLocationService.startWalk(principal.getName() , startWalkRequest.toService());
    }

    @MessageMapping("/api/v1/proposal")
    public void proposalWalk(Principal principal, @Payload @Valid ProposalWalkRequest proposalWalkRequest){
        walkLocationService.proposalWalk(principal.getName(), proposalWalkRequest.toService());
    }

    @MessageMapping("/api/v1/decision")
    public void decisionWalk(Principal principal, @Payload @Valid DecisionWalkRequest decisionWalkRequest){
        walkLocationService.decisionWalk(principal.getName(), decisionWalkRequest.toService());
    }

    @MessageMapping("/api/v1/walk-with")
    public void startWalkWith(Principal principal, @Payload @Valid StartWalkRequest startWalkRequest){
        walkLocationService.startWalkWith(principal.getName() , startWalkRequest.toService());
    }
}
