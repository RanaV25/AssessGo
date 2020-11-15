package com.assessgo.backend.repository;

import com.assessgo.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select distinct user from User user left join user.roles r " +
            "where lower(user.email) like lower(:email) " +
            "or lower(user.firstName) like lower(:firstName) " +
            "or lower(user.lastName) like lower(:lastName) " +
            "or lower(r.role) like lower(:role)")
    Page<User> findAllByFilter(@Param("email") String emailLike,
                               @Param("firstName") String firstNameLike,
                               @Param("lastName") String lastNameLike,
                               @Param("role") String roleLike,
                               Pageable pageable);

    User findByEmail(String email);



    User findByEmailIgnoreCase(String username);

    @Query("select user from User user inner join user.assessments assessment " +
            "where assessment.id = :assessmentId")
    Set<User> findByAssessmentId(@Param("assessmentId") Long accountId);


}
