package com.assessgo.backend.service;

import com.assessgo.backend.entity.Assessment;
import com.assessgo.backend.repository.AssessmentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AssessmentService implements FilterableCrudService<Assessment> {

    @Autowired
    private AssessmentRepository assessmentRepository;

    public AssessmentService() {
    }

    @Override
    public JpaRepository<Assessment, Long> getRepository() {
        return assessmentRepository;
    }

    @Override
    public Assessment save(Assessment entity) throws Exception {
        if (entity == null) {
            throw new Exception("Assessment entity cannot be empty or null");
        }
        Assessment assessmentAgainstName = assessmentRepository.findByAssessmentName(entity.getAssessmentName());
        if (assessmentAgainstName != null) {
            throw new Exception("Assessment already exist with name: " + entity.getAssessmentName());
        }
        return assessmentRepository.save(entity);
    }

    @Override
    public Assessment update(Assessment assessment) throws Exception {
        if (assessment == null) {
            throw new Exception("Assessment entity cannot be empty or null");
        }
        Optional<Assessment> existedEntity = assessmentRepository.findById(assessment.getId());
        if (!existedEntity.isPresent()) {
            throw new Exception("No assessment found against id:" + assessment.getId());
        }

        if (!assessment.getAssessmentName().equals(existedEntity.get().getAssessmentName())) {
            Assessment assessmentAgainstName = assessmentRepository
                    .findByAssessmentName(assessment.getAssessmentName());
            if (assessmentAgainstName != null) {
                throw new Exception("Assessment already exist with name: " + assessment.getAssessmentName());
            }
        }
        assessment.setVersion(existedEntity.get().getVersion());
        return assessmentRepository.save(assessment);
    }

    @Override
    public Page<Assessment> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<Assessment> results;
        if (filter.isPresent() && StringUtils.isNotBlank(filter.get())) {
            String searchValue = "%" + filter.get() + "%";
            results = assessmentRepository.findAllByFilter(searchValue, searchValue, pageable);
        } else {
            results = assessmentRepository.findAll(pageable);
        }
        return results;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }


}
