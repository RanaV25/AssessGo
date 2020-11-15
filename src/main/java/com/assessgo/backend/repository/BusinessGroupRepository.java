package com.assessgo.backend.repository;

import com.assessgo.backend.entity.BusinessGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author niraj
 */
@Repository
public interface BusinessGroupRepository extends JpaRepository<BusinessGroup, UUID>, JpaSpecificationExecutor<BusinessGroup> {

    BusinessGroup findByBusinessGroupName(String name);

    @Query("select distinct businessGroup from BusinessGroup businessGroup " +
            "where lower(businessGroup.name) like lower(:name) " +
            "or lower(businessGroup.description) like lower(:description)")
    Page<BusinessGroup> findAllByFilter(@Param("name") String nameLike,
                                        @Param("description") String descriptionLike,
                                        Pageable pageable);
}
