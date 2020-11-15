package com.assessgo.backend.service;



import com.assessgo.backend.entity.Report;
import com.assessgo.backend.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;



@Service
public class ReportService implements FilterableCrudService<Report> {

    public ReportService(){}

    @Autowired
    private ReportRepository quizRepository;

    @Override
    public Page<Report> findAnyMatching(Optional<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    @Override
    public JpaRepository<Report, Long> getRepository() {
        return quizRepository;
    }

    @Override
    public Report save(Report entity) throws Exception {
        return quizRepository.save(entity);
    }

    @Override
    public Report update(Report entity) throws Exception {
        Optional<Report> existedEntity = quizRepository.findById(entity.getId());
        entity.setVersion(existedEntity.get().getVersion());
        return quizRepository.save(entity);
    }
}
