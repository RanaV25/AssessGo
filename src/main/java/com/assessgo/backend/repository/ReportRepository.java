package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    @Query("select distinct report from Report report "
            + "where report.userId = :userId "
            + "and report.assessmentId = :assessmentId")
    Report findByUserIdAndAssessmentId(@Param("userId") Long userId, @Param("assessmentId") Long assessmentId);

    @Query("select report from Report report "
            + "where report.assessmentId = :assessmentId")
    Set<Report> findByAssessmentId(@Param("assessmentId") Long assessmentId);

}
