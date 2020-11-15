package com.assessgo.backend.dto;

import com.assessgo.backend.entity.Answer;
import com.assessgo.backend.entity.QuestionGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDto {
    private Long id;
    private String questionName;
    private String questionText;
    private String questionType;

    private String helpText;
    private String imagePath;

    private List<QuestionDto> questions;

    private QuestionGroup questionGroup;

    private Answer questionAnswer;
}
