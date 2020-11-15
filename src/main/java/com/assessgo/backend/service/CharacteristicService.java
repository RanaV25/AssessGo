package com.assessgo.backend.service;

import com.assessgo.backend.entity.Characteristic;
import com.assessgo.backend.repository.CharacteristicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CharacteristicService implements CrudService<Characteristic> {
    @Autowired
    private CharacteristicRepository characteristicRepository;

    public CharacteristicService() {}

    @Override
    public JpaRepository<Characteristic, Long> getRepository() {
        return characteristicRepository;
    }

    @Override
    public Characteristic save(Characteristic characteristic) throws Exception {
        return characteristicRepository.save(characteristic);
    }

    @Override
    public Characteristic update(Characteristic characteristic) throws Exception {
        Optional<Characteristic> existedEntity = characteristicRepository.findById(characteristic.getId());
        characteristic.setVersion(existedEntity.get().getVersion());
        return characteristicRepository.save(characteristic);
    }
}
