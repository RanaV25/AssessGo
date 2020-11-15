package com.assessgo.backend.service;

import com.assessgo.backend.entity.Role;
import com.assessgo.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
public class RoleService implements FilterableCrudService<Role> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Page<Role> findAnyMatching(Optional<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    @Override
    public JpaRepository<Role, Long> getRepository() {
        return null;
    }

    @Override
    public Role save(Role entity) throws Exception {
        return null;
    }

    @Override
    public Role update(Role entity) throws Exception {
        return null;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
