package com.assessgo.backend.dto;

import com.assessgo.backend.enums.UserValidation;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter

public class UserDto {

    private Long id;

    private String email;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private Set<RoleDto> roles;

    private Set<BusinessGroupDto> businessGroups;

    private String fullName;

    private UserValidation validationCode;

    private Set<AccountDto> accounts;

}
