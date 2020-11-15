package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ObjectiveRepository extends JpaRepository<Objective,Long> {

}
