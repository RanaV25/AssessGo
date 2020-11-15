package com.assessgo.backend.service;

import com.assessgo.backend.dto.BusinessGroupDto;
import com.assessgo.backend.dto.PageDto;
import com.assessgo.backend.dto.UserDto;
import com.assessgo.backend.entity.BusinessGroup;
import com.assessgo.backend.entity.User;
import com.assessgo.backend.repository.BusinessGroupRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author niraj
 */

@Service
@Transactional
public class BusinessGroupService {

    @Autowired
    private BusinessGroupRepository businessGroupRepository;

    @Autowired
    private ModelMapper mapper;

    public BusinessGroupService() {
    }

    public BusinessGroup save(BusinessGroup businessGroup) throws Exception {
        if (businessGroup == null) {
            throw new Exception("BusinessGroup entity cannot be empty or null");
        }
        BusinessGroup businessGroupAgainestName = businessGroupRepository.findByBusinessGroupName(businessGroup.getName());
        if (businessGroupAgainestName != null) {
            throw new Exception("BusinessGroup already exist with name: " + businessGroup.getName());
        }
        return businessGroupRepository.save(businessGroup);
    }

    public List<BusinessGroup> saveAll(List<BusinessGroup> entities) throws Exception {
        for (BusinessGroup businessGroup : entities) {
            if (businessGroup == null) {
                throw new Exception("BusinessGroup entity cannot be empty or null");
            }
            if ((businessGroup.getId() == null)) {
                BusinessGroup businessGroupAgainestName = businessGroupRepository.findByBusinessGroupName(businessGroup.getName());
                if (businessGroupAgainestName != null) {
                    throw new Exception("BusinessGroup already exist with name: " + businessGroup.getName());
                }
            }
        }

        return businessGroupRepository.saveAll(entities);
    }

    @Transactional
    public List<BusinessGroup> saveAll(BusinessGroupDto base, List<BusinessGroupDto> associates, Set<UserDto> users) throws Exception {
        if (base != null) {
            List<BusinessGroup> saveList = new ArrayList<>();
            List<User> saveUsers = users
                    .stream().map(userDto -> mapper.map(userDto, User.class))
                    .collect(Collectors.toList());
            if ((base.getId() == null)) {
                BusinessGroup businessGroupAgainestName = businessGroupRepository.findByBusinessGroupName(base.getName());
                if (businessGroupAgainestName != null) {
                    throw new Exception("BusinessGroup already exist with name: " + base.getName());
                }

                BusinessGroup master = mapper.map(base, BusinessGroup.class);
                List<BusinessGroup> subBusinessGroups = associates.stream().map(businessGroupDto -> load(businessGroupDto.getId())).collect(Collectors.toList());
                master.setSubBusinessGroup(subBusinessGroups);
                master.setUsers(saveUsers);
                saveList.add(master);
                saveList.addAll(subBusinessGroups);
            } else {
                BusinessGroup master = load(base.getId());
                master.getSubBusinessGroup().forEach(businessGroup -> {
                    businessGroup.setBusinessGroup(null);
                    businessGroupRepository.save(businessGroup);
                });
                List<BusinessGroup> subBusinessGroups = associates.stream().map(businessGroupDto -> load(businessGroupDto.getId())).collect(Collectors.toList());
                master.setSubBusinessGroup(subBusinessGroups);
                master.setUsers(saveUsers);
                saveList.add(master);
                saveList.addAll(subBusinessGroups);
            }

            return businessGroupRepository.saveAll(saveList);
        } else {
            throw new Exception("BusinessGroup entity cannot be empty or null");
        }
    }

    public BusinessGroup update(BusinessGroup businessGroup) throws Exception {
        if (businessGroup == null) {
            throw new Exception("BusinessGroup entity cannot be empty or null");
        }
        Optional<BusinessGroup> existedEntity = businessGroupRepository.findById(businessGroup.getId());
        if (!existedEntity.isPresent()) {
            throw new Exception("No business group found against id:" + businessGroup.getId());
        }

        if (!businessGroup.getName().equals(existedEntity.get().getName())) {
            BusinessGroup businessGroupAgainstName = businessGroupRepository.findByBusinessGroupName(businessGroup.getName());
            if (businessGroupAgainstName != null) {
                throw new Exception("BusinessGroup already exist with name: " + businessGroup.getName());
            }
        }

        return businessGroupRepository.save(businessGroup);
    }

    private BusinessGroup load(UUID id) {
        return businessGroupRepository.findById(id).orElse(null);
    }

    public BusinessGroup load(String id) {
        return load(UUID.fromString(id));
    }

    public Page<BusinessGroup> findAnyMatching(Optional<String> filter, Pageable pageable) {
        Page<BusinessGroup> results;
        if (filter.isPresent() && StringUtils.isNotBlank(filter.get())) {
            String searchValue = "%" + filter.get() + "%";
            results = businessGroupRepository.findAllByFilter(searchValue, searchValue, pageable);
        } else {
            results = businessGroupRepository.findAll(pageable);
        }
        return results;
    }

    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    public PageDto<BusinessGroupDto> getAllBusinessGroups(Long id, Integer page, Integer size) {

        Page<BusinessGroup> pagedBg = businessGroupRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(criteriaBuilder.not(criteriaBuilder.equal(root.get("id"), id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        }, PageRequest.of(page, size));

        PageDto<BusinessGroupDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(pagedBg.getTotalPages());
        pageDto.setContent(pagedBg.getContent().stream().map(businessGroup -> {
            BusinessGroupDto bgdto = mapper.map(businessGroup, BusinessGroupDto.class);
            if (businessGroup.getBusinessGroup() != null && businessGroup.getBusinessGroup().getId() != null) {
                bgdto.setParent(businessGroup.getBusinessGroup().getId().toString());
            }

            return bgdto;
        }).collect(Collectors.toList()));
        return pageDto;
    }

    public BusinessGroupDto find(String id) {
        BusinessGroup businessGroup = load(id);
        if (businessGroup != null) {
            BusinessGroupDto businessGroupDto = mapper.map(businessGroup, BusinessGroupDto.class);
            if (businessGroup.getSubBusinessGroup() != null) {
                List<BusinessGroupDto> subBusinessGroups = businessGroup.getSubBusinessGroup().stream().map(bg -> find(bg.getId())).collect(Collectors.toList());
                businessGroupDto.setBusinessGroupDtoList(subBusinessGroups);
            }

            return businessGroupDto;
        }

        return null;
    }

    public BusinessGroupDto find(UUID id) {
        BusinessGroup businessGroup = load(id);
        if (businessGroup != null) {
            BusinessGroupDto businessGroupDto = mapper.map(businessGroup, BusinessGroupDto.class);
            if (businessGroup.getSubBusinessGroup() != null) {
                List<BusinessGroupDto> subBusinessGroups = businessGroup.getSubBusinessGroup().stream().map(bg -> find(bg.getId())).collect(Collectors.toList());
                businessGroupDto.setBusinessGroupDtoList(subBusinessGroups);
            }

            return businessGroupDto;
        }

        return null;
    }

    public List<BusinessGroupDto> findAll() {
        List<BusinessGroup> businessGroups = businessGroupRepository.findAll();
        List<BusinessGroupDto> returnList = new ArrayList<>();

        businessGroups.forEach(businessGroup -> {
            if (businessGroup != null) {
                BusinessGroupDto businessGroupDto = mapper.map(businessGroup, BusinessGroupDto.class);
                if (businessGroup.getSubBusinessGroup() != null) {
                    List<BusinessGroupDto> subBusinessGroups = businessGroup.getSubBusinessGroup().stream().map(bg -> mapper.map(bg, BusinessGroupDto.class)).collect(Collectors.toList());
                    businessGroupDto.setBusinessGroupDtoList(subBusinessGroups);
                }

                if (businessGroup.getBusinessGroup() != null && businessGroup.getBusinessGroup().getId() != null) {
                    businessGroupDto.setParent(businessGroup.getBusinessGroup().getId().toString());
                }
                returnList.add(businessGroupDto);
            }
        });

        return returnList;
    }

    public BusinessGroupDto findParent(String id) {
        BusinessGroup businessGroup = load(id);

        if (businessGroup.getBusinessGroup() != null) {
            businessGroup = load(businessGroup.getBusinessGroup().getId());
        }

        if (businessGroup != null) {
            BusinessGroupDto businessGroupDto = mapper.map(businessGroup, BusinessGroupDto.class);
            if (businessGroup.getSubBusinessGroup() != null) {
                List<BusinessGroupDto> subBusinessGroups = businessGroup.getSubBusinessGroup().stream().map(bg -> find(bg.getId())).collect(Collectors.toList());
                businessGroupDto.setBusinessGroupDtoList(subBusinessGroups);
            }

            return businessGroupDto;
        }

        return null;
    }

    public void delete(String id) {
        BusinessGroup businessGroup = load(id);
        if (businessGroup != null) {
            businessGroupRepository.delete(businessGroup);
        }
    }
}
