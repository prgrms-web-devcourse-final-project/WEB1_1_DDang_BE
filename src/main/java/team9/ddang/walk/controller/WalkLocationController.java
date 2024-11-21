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
import team9.ddang.walk.controller.request.LocationRequest;
import team9.ddang.walk.service.WalkLocationService;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;
    private final MemberRepository memberRepository;

    @MessageMapping("/api/v1/location")
    public void updateUserLocationTest(@RequestBody @Valid LocationRequest locationRequest) {
        Member member = memberRepository.findById(1L).orElseThrow();
        walkLocationService.startWalk(member.getEmail() , locationRequest.toService());
    }
    // TODO : Security 적용
}
