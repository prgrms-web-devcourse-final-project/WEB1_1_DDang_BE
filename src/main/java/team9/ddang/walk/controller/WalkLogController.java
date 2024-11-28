package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.walk.controller.request.log.GetLogByDateRequest;
import team9.ddang.walk.service.WalkLogService;
import team9.ddang.walk.service.response.log.WalkLogResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
@Tag(name = "DDang DDang log API", description = "댕댕로그 API")
public class WalkLogController {

    private final WalkLogService walkLogService;

    @GetMapping("")
    public ApiResponse<List<LocalDate>> getWalkLogs(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<LocalDate> response = walkLogService.getWalkLogs(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @GetMapping("/date")
    public ApiResponse<List<WalkLogResponse>> getWalkLogByDate(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                         @RequestBody @Valid GetLogByDateRequest getLogByDateRequest){
        List<WalkLogResponse> response = walkLogService.getWalkLogByDate(customOAuth2User.getMember(), getLogByDateRequest.toService());
        return ApiResponse.ok(response);
    }

//    @GetMapping("/year")
//    public ApiResponse<?> getYearlyWalkLog(){
//
//    }
//
//    @GetMapping("/year/family")
//    public ApiResponse<?> getYearlyWalkLogByFamily(){
//
//    }
//
//    @GetMapping("/total")
//    public ApiResponse<?> getTotalWalkLog(){
//
//    }
//
//    @GetMapping("/month/stats")
//    public getMontlyWalkStats(){
//
//    }

}
