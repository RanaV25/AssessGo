package com.assessgo.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author niraj
 */

@Entity
@Table(name = "tbl_business_group")
public class BusinessGroup{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "business_group_name", nullable = false, unique = true)
    private String name;

    @Column(name = "business_group_description", nullable = false)
    @Size(max = 250)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_business_group_users",
            joinColumns = @JoinColumn(name = "business_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @JoinColumn(name = "subgroup", referencedColumnName = "id")
    @ManyToOne
    private BusinessGroup businessGroup;

    @OneToMany(mappedBy = "businessGroup")
    private List<BusinessGroup> subBusinessGroup = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public BusinessGroup getBusinessGroup() {
        return businessGroup;
    }

    public void setBusinessGroup(BusinessGroup businessGroup) {
        this.businessGroup = businessGroup;
    }

    @XmlTransient
    public List<BusinessGroup> getSubBusinessGroup() {
        return subBusinessGroup;
    }

    public void setSubBusinessGroup(List<BusinessGroup> subBusinessGroup) {
        this.subBusinessGroup.forEach(sbg -> sbg.setBusinessGroup(null));
        this.subBusinessGroup.clear();
        subBusinessGroup.forEach(sbg -> sbg.setBusinessGroup(this));
        this.subBusinessGroup.addAll(subBusinessGroup);
    }

}
