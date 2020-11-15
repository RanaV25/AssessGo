package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    Assessment findByAssessmentName(String name);

    @Query("select distinct assessment from Assessment assessment "
            + "where lower(assessment.assessmentName) like lower(:name) "
            + "or lower(assessment.assessmentDescription) like lower(:description)")
    Page<Assessment> findAllByFilter(@Param("name") String name, @Param("description") String description,
                                     Pageable pageable);



    @Query("select assessment from Assessment assessment inner join assessment.users users " +
            "where users.email = :userName")
    List<Assessment> findAnyMatchingAgainstUserName(@Param("userName") String userName);


    @Query("select assessment from Assessment assessment order by assessment.id asc")
    List<Assessment> findAllAssessmentsInDesc();

}
