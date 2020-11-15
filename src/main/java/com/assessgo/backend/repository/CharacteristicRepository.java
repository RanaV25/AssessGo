package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CharacteristicRepository extends JpaRepository<Characteristic,Long> {

}
