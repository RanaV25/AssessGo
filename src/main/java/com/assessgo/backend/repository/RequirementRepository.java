package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequirementRepository extends JpaRepository<Requirement,Long> {

    Requirement findByName(String name);
}
