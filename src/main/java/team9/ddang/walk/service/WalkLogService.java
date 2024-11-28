package team9.ddang.walk.service;

import team9.ddang.member.entity.Member;

import java.time.LocalDate;
import java.util.List;

public interface WalkLogService {
    List<LocalDate> getWalkLogs(Member member);
}
