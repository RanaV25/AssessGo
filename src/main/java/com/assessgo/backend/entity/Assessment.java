package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_assessment")
public class Assessment extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String assessmentName;

    @Column(nullable = true)
    private String assessmentType;

    @Column(nullable = false)
    private String assessmentDescription;

    @Column(nullable = true)
    private String timeLimit;

    @Column(nullable = true)
    private String timeToComplete;

    @Column(nullable = true)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column
    private String assessmentImagePath;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_assessment_roles",
            joinColumns = @JoinColumn(name = "assessment_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_assessment_users",
            joinColumns = @JoinColumn(name = "assessment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_assessment_accounts",
            joinColumns = @JoinColumn(name = "assessment_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> accounts;

    @Column(nullable = false)
    private String[] assignedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_assessments_questions",
            joinColumns = @JoinColumn(name = "assessment_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private Set<Question> questions = new HashSet<>();


}