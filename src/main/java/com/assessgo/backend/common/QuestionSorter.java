package com.assessgo.backend.common;



import com.assessgo.backend.entity.Question;

import java.util.Comparator;

public class QuestionSorter implements Comparator<Question> {
    @Override
    public int compare(Question o1, Question o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
