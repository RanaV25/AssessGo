package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Answer;
import com.assessgo.backend.entity.Question;
import com.assessgo.backend.entity.QuestionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
@Transactional
public interface QuestionRepository extends JpaRepository<Question,Long> {
    @Query("select distinct question from Question question "
            + "where lower(question.questionName) like lower(:name) "
            + "or lower(question.questionText) like lower(:description)")
    Page<Question> findAllByFilter(@Param("name") String name, @Param("description") String description,
                                   Pageable pageable);


    @Query("select question from Question question inner join question.assessmetns assessment " +
            "where assessment.id = :assessmentId")
    Set<Question> findAnyMatchingAgainstAssessmentId(@Param("assessmentId") Long accountId);

    @Query("select answers from Question question " +
            "where question.id = :questionId")
    Set<Answer> findAllMatchingAgainstQuestionId(@Param("questionId") Long questionId);

    @Query("select question from Question question inner join question.assessmetns assessment " +
            "where assessment.id = :assessmentId and question.questionGroup = :group ")
    Set<Question> findAllMatchingAgainstQuestionGroupId(@Param("group") QuestionGroup groupId, @Param("assessmentId") Long assessment);

    @Query("select question from Question question where question.isAssessmentQuestion = :isAssessmentQuestion ")
    Set<Question> findAllMatchingByType(@Param("isAssessmentQuestion") boolean isAssessmentQuestion);


    @Query("select question from Question question " +
            "where question.questionGroup = :group and question.questionType = :questionType ")
    Set<Question> findAllMatchingAgainstQuestionGroup(@Param("group") QuestionGroup group , @Param("questionType") String questionType);
}
