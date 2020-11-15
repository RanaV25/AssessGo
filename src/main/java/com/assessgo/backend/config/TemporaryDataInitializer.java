package com.assessgo.backend.config;

import com.assessgo.backend.entity.*;

import com.assessgo.backend.enums.RequirementTypeEnum;
import com.assessgo.backend.enums.RoleEnum;
import com.assessgo.backend.repository.*;
import com.assessgo.backend.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;


@Configuration
public class TemporaryDataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    /*Initializing the dummy data to use application. Will be removed when implement liquibase*/
    @PostConstruct
    public void init() {


        if (roleRepository.count() == 0) {

            List<Role> roles = new ArrayList<>();

            Arrays.stream(RoleEnum.values()).forEach(rolesEnum -> {
                Role role = new Role();
                role.setRole(rolesEnum.getValue());
                roles.add(role);
            });
            roleRepository.saveAll(roles);

        }



        if (userRepository.findByEmail("weave@weave.com") == null) {


            User user = new User();
            user.setEmail("admin@admin.com");
            user.setFirstName("Dummy");
            user.setLastName("User");
            user.setPasswordHash(passwordEncoder.encode("admin"));
            user.setRoles(new HashSet<>(roleRepository.findAll()));
            userRepository.save(user);
        }

        if(accountRepository.findByAccountName("Super Admin Account") == null) {
            Account account = new Account();
            account.setAccountName("Super Admin Account");
            Set<User> users = new HashSet<>();
            users.add(userRepository.findByEmail("weave@weave.com"));
            account.setUsers(users);
            try {
                accountRepository.save(account);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //addObjectives

        Objective objective1 = new Objective();
        objective1.setName("Objective 1");
        objective1.setDescription("This is the description of objective");
        Objective savedObjective1 = objectiveRepository.save(objective1);

        Objective objective2 = new Objective();
        objective2.setName("Objective 2");
        objective2.setDescription("This is the description of objective");
        Objective savedObjective2 = objectiveRepository.save(objective2);

        Objective objective3 = new Objective();
        objective3.setName("Objective 3");
        objective3.setDescription("This is the description of objective");
        Objective savedObjective3 = objectiveRepository.save(objective3);


        //add Characteristics

        Characteristic characteristic1 = new Characteristic();
        characteristic1.setName("Characteristic 1");
        characteristic1.setValue("1");
        Characteristic savedCharacteristc1 = characteristicRepository.save(characteristic1);

        Characteristic characteristic2 = new Characteristic();
        characteristic2.setName("Characteristic 2");
        characteristic2.setValue("2");
        Characteristic savedCharacteristc2 = characteristicRepository.save(characteristic2);

        Characteristic characteristic3 = new Characteristic();
        characteristic3.setName("Characteristic 3");
        characteristic3.setValue("3");
        Characteristic savedCharacteristc3 = characteristicRepository.save(characteristic3);


        //add strategy and plan
        Set<String> plan1 = new HashSet<>();
        plan1.add("Strategy And Plan 1");
        plan1.add("Strategy And Plan 2");
        plan1.add("Strategy And Plan 3");
        plan1.add("Strategy And Plan 4");
        plan1.add("Strategy And Plan 5");
        plan1.add("Strategy And Plan 6");


        //add stakeholders
        Set<String> stakeholder1 = new HashSet<>();
        stakeholder1.add("Stakeholder 1");
        stakeholder1.add("Stakeholder 2");
        stakeholder1.add("Stakeholder 3");
        stakeholder1.add("Stakeholder 4");
        stakeholder1.add("Stakeholder 5");
        stakeholder1.add("Stakeholder 6");
        stakeholder1.add("Stakeholder 7");




        //add requirements

        Requirement requirement1 = new Requirement();
        requirement1.setName("Requirement 1");
        requirement1.setRequirementDescription("This is requirement description");
        requirement1.setLastEdited(LocalDate.now().toString());
        requirement1.setType(RequirementTypeEnum.REQUIREMENT_TYPE_1.getValue());
        requirement1.getCharacteristics().add(savedCharacteristc1);
        requirement1.getObjectives().add(savedObjective1);
        try {
            requirementService.save(requirement1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement2 = new Requirement();
        requirement2.setName("Requirement 2");
        requirement2.setRequirementDescription("This is requirement description");
        requirement2.setLastEdited(LocalDate.now().toString());
        requirement2.getCharacteristics().add(savedCharacteristc2);
        requirement2.getObjectives().add(savedObjective2);
        requirement2.setStrategyAndPlan(plan1);
        requirement2.setStakeholders(stakeholder1);
        requirement2.setType(RequirementTypeEnum.REQUIREMENT_TYPE_2.getValue());
        try {
            requirementService.save(requirement2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement3 = new Requirement();
        requirement3.setName("Requirement 3");
        requirement3.setRequirementDescription("This is requirement description");
        requirement3.setLastEdited(LocalDate.now().toString());
        requirement3.setType(RequirementTypeEnum.REQUIREMENT_TYPE_3.getValue());
        requirement3.getCharacteristics().add(savedCharacteristc3);
        requirement3.getObjectives().add(savedObjective3);
        try {
            requirementService.save(requirement3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement4 = new Requirement();
        requirement4.setName("Requirement 4");
        requirement4.setRequirementDescription("This is requirement description");
        requirement4.setLastEdited(LocalDate.now().toString());
        requirement4.setType(RequirementTypeEnum.REQUIREMENT_TYPE_2.getValue());
        try {
            requirementService.save(requirement4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement5 = new Requirement();
        requirement5.setName("Requirement 5");
        requirement5.setRequirementDescription("This is requirement description");
        requirement5.setLastEdited(LocalDate.now().toString());
        requirement5.setType(RequirementTypeEnum.REQUIREMENT_TYPE_2.getValue());
        try {
            requirementService.save(requirement5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement6 = new Requirement();
        requirement6.setName("Requirement 6");
        requirement6.setRequirementDescription("This is requirement description");
        requirement6.setLastEdited(LocalDate.now().toString());
        requirement6.setType(RequirementTypeEnum.REQUIREMENT_TYPE_2.getValue());
        try {
            requirementService.save(requirement6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement7 = new Requirement();
        requirement7.setName("Requirement 7");
        requirement7.setRequirementDescription("This is requirement description");
        requirement7.setLastEdited(LocalDate.now().toString());
        requirement7.setType(RequirementTypeEnum.REQUIREMENT_TYPE_1.getValue());
        try {
            requirementService.save(requirement7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Requirement requirement8 = new Requirement();
        requirement8.setName("Requirement 8");
        requirement8.setRequirementDescription("This is requirement description");
        requirement8.setLastEdited(LocalDate.now().toString());
        requirement8.setType(RequirementTypeEnum.REQUIREMENT_TYPE_1.getValue());
        try {
            requirementService.save(requirement8);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
