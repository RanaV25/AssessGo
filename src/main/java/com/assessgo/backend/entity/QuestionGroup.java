package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "tbl_questionGroup")
public class QuestionGroup extends AbstractEntity{

    @Column(nullable = false)
    private String questionGroupName;

    @Column(nullable = false)
    private String definition;

    public String getQuestionGroupName() {
        return questionGroupName;
    }

    public void setQuestionGroupName(String questionGroupName) {
        this.questionGroupName = questionGroupName;
    }

    public String getFullDescription() {
        return questionGroupName + "(" + definition + ")";
    }


}