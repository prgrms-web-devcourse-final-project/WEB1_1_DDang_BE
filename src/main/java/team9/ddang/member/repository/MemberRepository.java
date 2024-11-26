package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.member.entity.Member;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
