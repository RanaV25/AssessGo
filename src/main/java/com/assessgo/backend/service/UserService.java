package com.assessgo.backend.service;


import com.assessgo.backend.dto.RoleDto;
import com.assessgo.backend.dto.UserDto;
import com.assessgo.backend.entity.User;
import com.assessgo.backend.repository.BusinessGroupRepository;
import com.assessgo.backend.repository.RoleRepository;
import com.assessgo.backend.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserService implements FilterableCrudService<User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    private BusinessGroupRepository businessGroupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;


    public UserService() {
    }

    @Override
    public JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public User save(User entity) throws Exception {
        if (entity == null) {
            throw new Exception("User entity cannot be empty or null");
        }
        User userAgainstEmail = userRepository.findByEmail(entity.getEmail());
        if (userAgainstEmail != null) {
            throw new Exception("User already exist with email: " + entity.getEmail());
        }
        entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        return userRepository.save(entity);
    }

    @Override
    public User update(User user) throws Exception {
        Optional<User> existedEntity = validateUpdateRequest(user);

        user.setVersion(existedEntity.get().getVersion());
        return userRepository.save(user);
    }

    private Optional<User> validateUpdateRequest(User user) throws Exception {
        if (user == null) {
            throw new Exception("User entity cannot be empty or null");
        }
        Optional<User> existedEntity = userRepository.findById(user.getId());
        if (!existedEntity.isPresent()) {
            throw new Exception("No user found against id:" + user.getId());
        }

        if (!user.getEmail().equals(existedEntity.get().getEmail())) {
            User userAgainstEmail = userRepository.findByEmail(user.getEmail());
            if (userAgainstEmail != null) {
                throw new Exception("User already exist with email: " + user.getEmail());
            }
        }
        return existedEntity;
    }

    @Override
    public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<User> results;
        if (filter.isPresent() && StringUtils.isNotBlank(filter.get())) {
            String searchValue = "%" + filter.get() + "%";
            results = userRepository.findAllByFilter(searchValue, searchValue, searchValue, searchValue, pageable);
        } else {
            results = userRepository.findAll(pageable);
        }
        return results;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    /**
     * Used only to update user profile for current logged in user
     *
     * @param user
     * @return
     * @throws Exception
     */
    public User profileUpdate(User user) throws Exception {

        Optional<User> existedEntity = validateUpdateRequest(user);

        User existedUser = existedEntity.get();
        existedUser.setLastName(user.getLastName());
        existedUser.setFirstName(user.getFirstName());
        existedUser.setEmail(user.getEmail());
        return userRepository.save(existedUser);
    }

    public void updatePassword(String email, String password) throws Exception {

        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            throw new Exception("Not able to update password with empty email or password values.");
        }

        User userAgainstEmail = userRepository.findByEmail(email);
        if (userAgainstEmail == null) {
            throw new Exception("No User found against email: " + email);
        }

        userAgainstEmail.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(userAgainstEmail);
    }


    public List<UserDto> saveAll(List<UserDto> users) throws Exception {
        List<User> entities = users.stream().map(user -> {
                    User entity = mapper.map(user, User.class);
                    entity.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
                    if (user.getRoles() != null) {
                        entity.setRoles(user.getRoles().stream().map(role -> roleRepository.findByRole(role.getRole())).collect(Collectors.toSet()));
                    }

                    return entity;
                }
        ).collect(Collectors.toList());
        entities = userRepository.saveAll(entities);
        return entities.stream().map(user -> {
            UserDto udto = mapper.map(user, UserDto.class);
            if (user.getRoles() != null) {
                udto.setRoles(user.getRoles().stream().map(role -> mapper.map(role, RoleDto.class)).collect(Collectors.toSet()));
            }

            return udto;
        }).collect(Collectors.toList());
    }


    public Set<User> getAllUsers() {
        return new HashSet<>(getRepository().findAll());
    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return mapper.map(user, UserDto.class);
        }

        return null;
    }
}
