package com.assessgo.backend.repository;

import com.assessgo.backend.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountName(String name);

    @Query("select distinct account from Account account " +
            "where lower(account.accountName) like lower(:name) " +
            "or lower(account.accountDescription) like lower(:description)")
    Page<Account> findAllByFilter(@Param("name") String emailLike,
                                  @Param("description") String firstNameLike,
                                  Pageable pageable);


    @Query("select account from Account account inner join account.users users " +
            "where users.email = :userName")
    Set<Account> findAnyMatchingAgainstUserName(@Param("userName") String userName);
}
