package team9.ddang.walk.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team9.ddang.global.api.ApiResponse;
import team9.ddang.member.oauth2.CustomOAuth2User;
import team9.ddang.walk.service.WalkLogService;

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

//    @GetMapping("/date")
//    public ApiResponse<?> getWalkLogByDate(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
//                                           ){
//    }
//
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
