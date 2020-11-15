package com.assessgo.backend.common;


import com.assessgo.backend.entity.Answer;

import java.util.Comparator;

public class AnswerSorter implements Comparator<Answer> {
    @Override
    public int compare(Answer o1, Answer o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
