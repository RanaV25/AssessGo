package com.assessgo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_account")
public class Account extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String accountName;

    @Column(nullable = true)
    @Size(max = 150)
    private String accountDescription;

    @Column(name = "avatar")
    private String profileImage;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_account_users",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;


}
