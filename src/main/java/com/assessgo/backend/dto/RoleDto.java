package com.assessgo.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RoleDto {


    private Long id;
    private String role;

    private List<RoleDto> roles;


}
