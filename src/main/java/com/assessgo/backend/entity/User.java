package com.assessgo.backend.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "tbl_user")
public class User extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 4, max = 255)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Column(nullable = false)
    private Set<Role> roles;

    @ManyToMany(mappedBy = "users",fetch = FetchType.EAGER)
    private List<Account> accounts = new ArrayList<Account>();

    @ManyToMany(mappedBy="users")
    private List<Assessment> assessments;
}
