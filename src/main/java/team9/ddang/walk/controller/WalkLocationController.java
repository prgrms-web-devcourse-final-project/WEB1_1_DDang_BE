package team9.ddang.walk.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import team9.ddang.walk.controller.request.LocationRequest;
import team9.ddang.walk.service.WalkLocationService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WalkLocationController {

    private final WalkLocationService walkLocationService;

    @MessageMapping("/location")
    public void updateUserLocationTest(LocationRequest locationRequest) {
        walkLocationService.saveMemberLocation(1L , locationRequest.toService());
    }
}
