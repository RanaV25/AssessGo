package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="tbl_characteristic")
public class Characteristic extends AbstractEntity {
    @Column
    String name;

    @Column
    String value;


}
