package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="tbl_objective")
public class Objective extends AbstractEntity {
    @Column
    String name;

    @Column
    String description;


}
