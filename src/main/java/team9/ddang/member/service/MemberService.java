package team9.ddang.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import team9.ddang.member.entity.IsMatched;
import team9.ddang.member.service.request.JoinServiceRequest;
import team9.ddang.member.service.request.UpdateAddressServiceRequest;
import team9.ddang.member.service.request.UpdateServiceRequest;
import team9.ddang.member.service.response.MemberResponse;
import team9.ddang.member.service.response.MyPageResponse;
import team9.ddang.member.service.response.UpdateResponse;

public interface MemberService {

    MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response);

    String reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

    String logout(HttpServletRequest request);

    MyPageResponse getMemberInfo(Long memberId);

    IsMatched updateIsMatched(Long memberId, IsMatched isMatched);

    UpdateResponse updateMember(Long memberId, UpdateServiceRequest updateServiceRequest);

    UpdateResponse getUpdateInfo(Long memberId);

    void deleteMember(Long memberId);

    void updateAddress(Long memberId, UpdateAddressServiceRequest serviceRequest);
}
