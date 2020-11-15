package com.assessgo.backend.dto;

import com.assessgo.backend.entity.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter

public class AssessmentDto {

    private Long id;

    @NotBlank(message = "Assessment Number is mandatory")
    private String assessmentName;

    @NotBlank(message = "Assessment Description is mandatory")
    @Size(max = 150, message = "Max length is 150")
    private String assessmentDescription;

    @NotBlank(message = "Assessment Type is mandatory")
    private String assessmentType;

    @NotBlank(message = "Time Limit is mandatory")
    private String timeLimit;


    @NotBlank(message = "Time to complete is mandatory")
    private String timeToComplete;


    @NotBlank(message = "Start Date is mandatory")
    private LocalDateTime startDate;

    @NotBlank(message = "End date is mandatory")
    private LocalDateTime endDate;


    private Set<AssessmentDto> assessmentDtoList;

    private Set<Role> role;

    private String[] assignedBy;

    private String assessmentImagePath;

}
