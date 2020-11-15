package com.assessgo.backend.common;

import com.assessgo.backend.entity.Assessment;

import java.util.Comparator;

public class AssessmentSorter implements Comparator<Assessment>
{
    @Override
    public int compare(Assessment o1, Assessment o2) {
        return o1.getId().compareTo(o2.getId());
    }
}