package team9.ddang.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import team9.ddang.member.service.request.JoinServiceRequest;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;

public interface MemberService {

    MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response);

    String reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

    String logout(HttpServletRequest request);

    MyPageResponse getMemberInfo(Long memberId);
}
