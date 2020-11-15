package com.assessgo.backend.service;

import com.assessgo.backend.entity.QuestionGroup;
import com.assessgo.backend.repository.QuestionGroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionGroupService implements FilterableCrudService<QuestionGroup> {

    @Autowired
    private QuestionGroupRepository questionGroupRepository;
    public QuestionGroupService() {
    }

    @Override
    public JpaRepository<QuestionGroup, Long> getRepository() {
        return questionGroupRepository;
    }

    @Override
    public QuestionGroup save(QuestionGroup entity) throws Exception {
        if(entity == null) throw new Exception("Question entity cannot be null");
        return questionGroupRepository.save(entity);
    }

    @Override
    public QuestionGroup update(QuestionGroup entity) throws Exception {
        if(entity == null) throw new Exception("Question entity cannot be bull");
        Optional<QuestionGroup> existedEntity = questionGroupRepository.findById(entity.getId());
        entity.setVersion(existedEntity.get().getVersion());
        return questionGroupRepository.save(entity);
    }


    @Override
    public Page<QuestionGroup> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<QuestionGroup> results;
        results = questionGroupRepository.findAll(pageable);
        return results;
    }

    public List<QuestionGroup> findAnyMatching() {
        List<QuestionGroup> results;

        results = questionGroupRepository.findAll();

        return results;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }
}
