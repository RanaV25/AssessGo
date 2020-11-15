package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "tbl_requirement")
public class Requirement extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String requirementDescription;

    @Column
    private String LastEdited = LocalDate.now().toString();

    @Column
    private String type;

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> strategyAndPlan;

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> stakeholders;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable
    private Set<Objective> objectives = new HashSet<>();

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable
    private Set<Characteristic> characteristics = new HashSet<>();


}
