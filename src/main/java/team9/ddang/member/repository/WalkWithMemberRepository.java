package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.member.entity.WalkWithMember;

public interface WalkWithMemberRepository extends JpaRepository<WalkWithMember, Long> {
}
