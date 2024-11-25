package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import team9.ddang.member.entity.Member;
import team9.ddang.member.repository.MemberRepository;
import team9.ddang.walk.controller.request.DecisionWalkRequest;
import team9.ddang.walk.controller.request.ProposalWalkRequest;
import team9.ddang.walk.controller.request.StartWalkRequest;
import team9.ddang.walk.service.WalkLocationService;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;
    private final MemberRepository memberRepository;

    @MessageMapping("/api/v1/walk-alone")
    public void startWalk(@RequestBody @Valid StartWalkRequest startWalkRequest) {
        walkLocationService.startWalk("michael.brown@example.com" , startWalkRequest.toService());
    }
    // TODO : Security 적용

    @MessageMapping("/api/v1/proposal")
    public void proposalWalk(@RequestBody @Valid ProposalWalkRequest proposalWalkRequest){
        Member member = memberRepository.findByEmail("michael.brown@example.com")
                .orElseThrow();

        walkLocationService.proposalWalk(member, proposalWalkRequest.toService());
    }

    @MessageMapping("/api/v1/decision")
    public void decisionWalk(@RequestBody @Valid DecisionWalkRequest decisionWalkRequest){
        Member member = memberRepository.findByEmail("michael.brown@example.com")
                .orElseThrow();

        walkLocationService.decisionWalk(member, decisionWalkRequest.toService());
    }

    @MessageMapping("/api/v1/walk-with")
    public void startWalkWith(){

    }
}
