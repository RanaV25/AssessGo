package com.assessgo.backend.service;

import com.assessgo.backend.entity.Account;
import com.assessgo.backend.repository.AccountRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements FilterableCrudService<Account> {

    @Autowired
    private AccountRepository accountRepository;

    public AccountService() {
    }

    @Override
    public JpaRepository<Account, Long> getRepository() {
        return accountRepository;
    }

    @Override
    public Account save(Account entity) throws Exception {
        if (entity == null) {
            throw new Exception("Account entity cannot be empty or null");
        }
        Account accountAgainstName = accountRepository.findByAccountName(entity.getAccountName());
        if(accountAgainstName != null) {
            throw new Exception("Account already exist with name: " + entity.getAccountName());
        }
        return accountRepository.save(entity);
    }

    @Override
    public Account update(Account account) throws Exception {
        if (account == null) {
            throw new Exception("Account entity cannot be empty or null");
        }
        Optional<Account> existedEntity = accountRepository.findById(account.getId());
        if (!existedEntity.isPresent()) {
            throw new Exception("No account found against id:" + account.getId());
        }

        if(!account.getAccountName().equals(existedEntity.get().getAccountName())){
            Account accountAgainstName = accountRepository.findByAccountName(account.getAccountName());
            if(accountAgainstName != null) {
                throw new Exception("Account already exist with name: " + account.getAccountName());
            }
        }
        account.setVersion(existedEntity.get().getVersion());
        return accountRepository.save(account);
    }

    @Override
    public Page<Account> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<Account> results;
        if (filter.isPresent() && StringUtils.isNotBlank(filter.get())) {
            String searchValue = "%" + filter.get() + "%";
            results = accountRepository.findAllByFilter(searchValue, searchValue, pageable);
        } else {
            results = accountRepository.findAll(pageable);
        }
        return results;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }




}
