package com.assessgo.backend.service;

import com.assessgo.backend.entity.Question;
import com.assessgo.backend.repository.QuestionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements FilterableCrudService<Question>{

    @Autowired
    private QuestionRepository questionRepository;

    public QuestionService(){

    }

    @Override
    public JpaRepository<Question, Long> getRepository() {
        return questionRepository;
    }

    @Override
    public Question save(Question entity) throws Exception {
        if(entity == null) throw new Exception("Question entity cannot be null");
        return questionRepository.save(entity);
    }

    @Override
    public Question update(Question entity) throws Exception {
        if(entity == null) throw new Exception("Question entity cannot be bull");
        Optional<Question> existedEntity = questionRepository.findById(entity.getId());
        entity.setVersion(existedEntity.get().getVersion());
        return questionRepository.save(entity);
    }


    @Override
    public Page<Question> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<Question> results;
        if (filter.isPresent() && StringUtils.isNotBlank(filter.get())) {
            String searchValue = "%" + filter.get() + "%";
            results = questionRepository.findAllByFilter(searchValue, searchValue, pageable);
        } else {
            results = questionRepository.findAll(pageable);
        }
        return results;
    }

    public List<Question> findAnyMatching() {
        List<Question> results;

        results = questionRepository.findAll();

        return results;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }
}
