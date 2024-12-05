package team9.ddang.member.service;

import team9.ddang.member.controller.request.AddFriendRequest;
import team9.ddang.member.entity.Member;
import team9.ddang.member.service.response.FriendListResponse;
import team9.ddang.member.service.response.FriendResponse;
import team9.ddang.member.service.response.MemberResponse;

import java.util.List;

public interface FriendService {
    MemberResponse decideFriend(Member member, AddFriendRequest addFriendRequest);

    List<FriendListResponse> getFriendList(Member member);

    FriendResponse getFriend(Member member, Long memberId);

    void deleteFriend(Member member, Long memberId);
}
