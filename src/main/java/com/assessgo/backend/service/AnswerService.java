package com.assessgo.backend.service;

import com.assessgo.backend.entity.Answer;
import com.assessgo.backend.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService implements FilterableCrudService<Answer> {

    @Autowired
    private AnswerRepository answerRepository;

    public AnswerService(){}

    @Override
    public JpaRepository<Answer, Long> getRepository() {
        return answerRepository;
    }

    @Override
    public Answer save(Answer entity) throws Exception {
        if(entity == null) throw new Exception("Answer entity cannot be null");
        return answerRepository.save(entity);
    }

    @Override
    public Answer update(Answer entity) throws Exception {
        if(entity == null) throw new Exception("Answer entity cannot be bull");
        Optional<Answer> existedEntity = answerRepository.findById(entity.getId());
        entity.setVersion(existedEntity.get().getVersion());
        return answerRepository.save(entity);
    }


    @Override
    public Page<Answer> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<Answer> results;
        results = answerRepository.findAll(pageable);
        return results;
    }

    public List<Answer> findAnyMatching() {
        List<Answer> results;

        results = answerRepository.findAll();

        return results;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }



}