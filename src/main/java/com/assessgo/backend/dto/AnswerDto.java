package com.assessgo.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class AnswerDto {
    private Long id;
    private String answer;
    private boolean isCorrect;
}
