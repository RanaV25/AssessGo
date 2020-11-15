package com.assessgo.backend.service;

import com.assessgo.backend.entity.Requirement;
import com.assessgo.backend.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class RequirementService implements FilterableCrudService<Requirement>{

    @Autowired
    private RequirementRepository requirementRepository;

    public RequirementService() {}

    @Override
    public Page<Requirement> findAnyMatching(Optional<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    @Override
    public JpaRepository<Requirement, Long> getRepository() {
        return requirementRepository;
    }

    @Override
    public Requirement save(Requirement entity) throws Exception {
        if (entity == null) {
            throw new Exception("Requirement entity cannot be empty or null");
        }
        return requirementRepository.save(entity);
    }

    @Override
    public Requirement update(Requirement entity) throws Exception {
        if (entity == null) {
            throw new Exception("Requirement entity cannot be empty or null");
        }
        Optional<Requirement> existedEntity = requirementRepository.findById(entity.getId());
        if (!existedEntity.isPresent()) {
            throw new Exception("No Requirement found against id:" + entity.getId());
        }

        entity.setVersion(existedEntity.get().getVersion());
        entity.setLastEdited(LocalDate.now().toString());
        return requirementRepository.save(entity);
    }
}
