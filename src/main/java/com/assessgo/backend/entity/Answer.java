package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "tbl_answer")
public class Answer extends AbstractEntity {

    @Column(nullable = false)
    @Lob
    private String answer;

    private boolean isCorrect;

    @Column(nullable = true)
    private Double answerScore;

    @Column(nullable = true)
    private String answerScoreName;


}

