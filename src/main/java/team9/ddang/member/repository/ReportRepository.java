package team9.ddang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.ddang.member.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
