package team9.ddang.member.service;

import team9.ddang.member.entity.Member;
import team9.ddang.member.service.response.MainResponse;

public interface MainService {
    MainResponse getMain(Member member);
}
