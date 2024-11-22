package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import team9.ddang.walk.controller.request.StartWalkRequest;
import team9.ddang.walk.service.WalkLocationService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;

    @MessageMapping("/location")
    public void startWalk(@RequestBody @Valid StartWalkRequest startWalkRequest) {
        walkLocationService.startWalk("michael.brown@example.com" , startWalkRequest.toService());
    }
    // TODO : Security 적용
}
