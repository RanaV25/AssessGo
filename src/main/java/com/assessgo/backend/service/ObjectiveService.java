package com.assessgo.backend.service;

import com.assessgo.backend.entity.Objective;
import com.assessgo.backend.repository.ObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ObjectiveService implements CrudService<Objective> {
    @Autowired
    private ObjectiveRepository objectiveRepository;


    public ObjectiveService() {}

    @Override
    public JpaRepository<Objective, Long> getRepository() {
        return objectiveRepository;
    }

    @Override
    public Objective save(Objective objective) throws Exception {
        return objectiveRepository.save(objective);
    }

    @Override
    public Objective update(Objective objective) throws Exception {
        Optional<Objective> existedEntity = objectiveRepository.findById(objective.getId());
        objective.setVersion(existedEntity.get().getVersion());
        return objectiveRepository.save(objective);
    }
}
