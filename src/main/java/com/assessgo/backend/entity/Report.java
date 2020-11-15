package com.assessgo.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_report")
public class Report extends AbstractEntity{
    @Column
    private Long userId;

    @Column
    private Long assessmentId;

    @Column(length=1000)
    private Long[] attemptedQuestions;

    @Column(length=1000)
    private Long[] givenAnswers;

    @Column
    private boolean isCompleted;

    @Column
    private boolean isInProgress;



    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long[] getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public void setAttemptedQuestions(Long[] attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }

    public Long[] getGivenAnswers() {
        return givenAnswers;
    }

    public void setGivenAnswers(Long[] givenAnswers) {
        this.givenAnswers = givenAnswers;
    }
}
