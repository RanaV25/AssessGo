package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_question")
public class Question  extends AbstractEntity {

    @Column(unique = false, nullable = false)
    private String questionName;

    @Column
    private String questionType;

    @Column(unique = false, nullable = false)
    @Lob
    private String questionText;


    @Column(unique = false, nullable = true)
    private String helpText;

    @Column(unique = false, nullable = true)
    private String imagePath;


    @ManyToMany(fetch = FetchType.EAGER,
            mappedBy = "questions")
    private Set<Assessment> assessmetns;


    @OneToOne
    @JoinColumn()
    private QuestionGroup questionGroup;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_question_answers",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_id"))
    @Column(nullable = false)
    private Set<Answer> answers;

    @Column
    private boolean isAssessmentQuestion;

}
