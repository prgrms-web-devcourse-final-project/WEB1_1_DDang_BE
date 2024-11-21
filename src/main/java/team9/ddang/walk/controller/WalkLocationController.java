package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import team9.ddang.walk.controller.request.LocationRequest;
import team9.ddang.walk.service.WalkLocationService;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;

    @MessageMapping("/location")
    public void updateUserLocationTest(LocationRequest locationRequest) {
        walkLocationService.saveMemberLocation(1L , locationRequest.toService());
    } // TODO : Security 적용
}
