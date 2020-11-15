package com.assessgo.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author niraj
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessGroupDto extends GraphModel {

    private UUID id;

    private UUID parent;

    private String name;

    private String description;

    private List<UserDto> users;

    private List<BusinessGroupDto> businessGroupDtoList;

    public BusinessGroupDto() {
    }

    public BusinessGroupDto(String id, String name, String description) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.description = description;
        this.businessGroupDtoList = new ArrayList<>();
    }

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        if (StringUtils.isEmpty(id)) {
            this.id = null;
        } else {
            this.id = UUID.fromString(id);
        }
    }

    public String getParent() {
        return parent != null ? parent.toString() : null;
    }

    public void setParent(String parent) {
        this.parent = UUID.fromString(parent);
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

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }

    public List<BusinessGroupDto> getBusinessGroupDtoList() {
        return businessGroupDtoList;
    }

    public void setBusinessGroupDtoList(List<BusinessGroupDto> businessGroupDtoList) {
        this.businessGroupDtoList = businessGroupDtoList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof BusinessGroupDto)) {
            return false;
        }
        BusinessGroupDto other = (BusinessGroupDto) obj;
        return id.equals(other.getId());
    }
}
