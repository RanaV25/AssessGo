package com.assessgo.frontend.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.notification.Notification;
import com.assessgo.backend.dto.RoleDto;
import com.assessgo.backend.dto.UserDto;
import com.assessgo.backend.enums.UserValidation;
import com.assessgo.backend.service.UserService;
import com.assessgo.backend.util.StringUtil;

import com.assessgo.frontend.components.csv.CSVUserGrid;
import com.assessgo.frontend.util.delegate.IAction;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVUploadView extends UploadView {

    private ModelMapper mapper = new ModelMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVUploadView.class);

    private UserService userService;
    private ConfirmDialog dialog;
    private CSVUserGrid csvUserGrid;

    private IAction action;
    private List<UserDto> users;

    public CSVUploadView(IAction action, UserService userService) {
        this.action = action;
        this.userService = userService;
        this.dialog = new ConfirmDialog("Confirm delete",
                "Are you sure you want to delete the item?",
                "Delete", this::onDelete, "Cancel", cancelEvent -> dialog.close());
    }

    @Override
    public Component processInput(InputStream stream) {
        try {
            List<String> lines = IOUtils.readLines(stream, StandardCharsets.UTF_8);
            if (lines != null) {
                users = lines.stream().skip(1).map(s -> toUser(s)).collect(Collectors.toList());
                return createUsersGrid();
            }
        } catch (IOException ex) {
            LOGGER.error("Exception reading stream", ex);
        }
        return null;
    }

    private UserDto toUser(String line) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }

        String[] userAttr = line.split(",");

        UserDto userDto = new UserDto();
        userDto.setEmail(userAttr[0]);
        userDto.setFirstName(userAttr[1]);
        userDto.setLastName(userAttr[2]);
        userDto.setPasswordHash(userAttr[3]);

        String[] roleNames = userAttr[4].split(":");
        if (roleNames != null) {
            userDto.setRoles(Arrays.asList(roleNames).stream().map(s -> {
                RoleDto roleDto = new RoleDto();
                roleDto.setRole(s);
                return roleDto;
            }).collect(Collectors.toSet()));
        }

        return userDto;
    }

    private Component createUsersGrid() {
        csvUserGrid = new CSVUserGrid();
        csvUserGrid.setUsers(verifyAll(users));
        csvUserGrid.addSaveListener(event -> {
            if (save(users)) {
                action.save();
            }
        });

        csvUserGrid.addCancelListener(event -> action.cancel());
        csvUserGrid.addDeleteListener(event -> dialog.open());

        if (!validate(users)) {
            Notification.show("One or more user entries are violated user rules.Please correct them and submit again.");
        }

        return csvUserGrid;
    }

    private List<UserDto> verifyAll(List<UserDto> users) {
        if (users != null && users.size() > 0) {
            users.stream().forEach(userDto -> {
                if (org.springframework.util.StringUtils.isEmpty(userDto.getFirstName())) {
                    userDto.setValidationCode(UserValidation.FIRST_NAME_REQUIRED);
                } else if (org.springframework.util.StringUtils.isEmpty(userDto.getLastName())) {
                    userDto.setValidationCode(UserValidation.LAST_NAME_REQUIRED);
                } else if (org.springframework.util.StringUtils.isEmpty(userDto.getEmail())) {
                    userDto.setValidationCode(UserValidation.USERNAME_REQUIRED);
                } else if (userService.findByEmail(userDto.getEmail()) != null) {
                    userDto.setValidationCode(UserValidation.USERNAME_ALREADY_EXISTS);
                } else if (org.springframework.util.StringUtils.isEmpty(userDto.getPasswordHash())) {
                    userDto.setValidationCode(UserValidation.PASSWORD_NOT_FOUND);
                } else if (!StringUtil.isValidPassword(userDto.getPasswordHash())) {
                    userDto.setValidationCode(UserValidation.NEED_A_STRONG_PASSWORD);
                } else if (!StringUtil.isValidEmail(userDto.getEmail())) {
                    userDto.setValidationCode(UserValidation.INVALID_EMAIL);
                } else {
                    userDto.setValidationCode(UserValidation.VALID_USER);
                }
            });
        }

        users.sort((o1, o2) -> {
            if (o1.getValidationCode() == UserValidation.VALID_USER) {
                return 1;
            } else {
                return -1;
            }
        });

        return users;
    }

    private boolean validate(List<UserDto> users) {
        if (users != null && users.size() > 0) {
            return !users.stream().filter(userDto -> userDto.getValidationCode() != UserValidation.VALID_USER).findAny().isPresent();
        }

        return true;
    }

    private boolean save(List<UserDto> users) {
        if (users != null && users.size() > 0) {
            try {
                users = verifyAll(users);
                if (validate(users)) {
                    users = this.userService.saveAll(users);

                    Notification.show("Users are imported Successfully");
                    return true;
                } else {

                    Notification.show("One or more user entries are violated user rules.Please correct them and submit again.");
                }
            } catch (Exception ex) {

                Notification.show(String.format("Users importing failed : {}", ex.getMessage()));
            }
        }

        return false;
    }

    private void removeSelectedItems(List<UserDto> selected, List<UserDto> users) {
        if (selected != null && !selected.isEmpty()) {
            selected.forEach(userDto -> {
                users.remove(userDto);
            });
        }
    }

    private void onDelete(ConfirmDialog.ConfirmEvent event) {
        removeSelectedItems(csvUserGrid.getSelectedItems(), users);
        csvUserGrid.setUsers(users);
    }


}
