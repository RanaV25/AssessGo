package com.assessgo.frontend.views.user;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.assessgo.MainView;
import com.assessgo.backend.common.Role;
import com.assessgo.backend.dto.AccountDto;
import com.assessgo.backend.dto.RoleDto;
import com.assessgo.backend.dto.UserDto;
import com.assessgo.backend.entity.Account;
import com.assessgo.backend.entity.User;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.AccountService;
import com.assessgo.backend.service.RoleService;
import com.assessgo.backend.service.UserService;
import com.assessgo.frontend.components.PaginationComponent;
import com.assessgo.frontend.util.CSVUploadView;
import com.assessgo.frontend.util.delegate.IAction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;



@Tag("user-view")
@JsModule("./src/views/user/user-view.js")
@Route(value = "user", layout = MainView.class)

public class UserView extends PolymerTemplate<UserView.UserViewModel> implements AfterNavigationObserver {


    @Id("btnAddUser")
    private Button btnAddUser;
    @Id("btnImportUser")
    private Button btnImportUser;
    @Id("btnDownloadUsers")
    private Button btnDownloadUsers;
    @Id("userFormLayout")
    private FormLayout userFormLayout;
    @Id("userGrid")
    private Grid<UserDto> userGrid;
    @Id("txtPassword")
    private PasswordField txtPassword;
    @Id("txtEmail")
    private TextField txtEmail;
    @Id("txtLastName")
    private TextField txtLastName;
    @Id("txtFirstName")
    private TextField txtFirstName;
    @Id("btnCancel")
    private Button btnCancel;
    @Id("btnAdd")
    private Button btnAdd;
    @Id("role")
    private MultiselectComboBox<RoleDto> comboRoles;
    @Id("accountComboBox")
    private MultiselectComboBox<AccountDto> accountComboBox;
    @Id("mainContainer")
    private VerticalLayout mainContainer;

    private Binder<UserDto> userBinder = new Binder<>(UserDto.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AccountService accountService;

    @Id("paginationComponent")
    private PaginationComponent pagination;

    @Id("txtSearch")
    private TextField txtSearch;


    private ModelMapper mapper = new ModelMapper();



    public interface UserViewModel extends TemplateModel {

    }


    public UserView() {
        initGrid();
        initBinder();

        pagination.resetPaginationVariable(pagination.getDdPageSize());
        paginationClickListeners(pagination);

        txtSearch.setValueChangeMode(ValueChangeMode.EAGER);
        txtFirstName.setValueChangeMode(ValueChangeMode.EAGER);
        txtLastName.setValueChangeMode(ValueChangeMode.EAGER);
        txtEmail.setValueChangeMode(ValueChangeMode.EAGER);
        txtPassword.setValueChangeMode(ValueChangeMode.EAGER);
        accountComboBox.setVisible(false);
    }


    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

        comboRoles.setItemLabelGenerator(RoleDto::getRole);

        accountComboBox.setItemLabelGenerator(AccountDto::getAccountName);

        accountComboBox.setItems(accountService.getRepository().findAll()
        .stream().map(entity->mapper.map(entity,AccountDto.class)));

                List<RoleDto> roleDtos = new ArrayList<>();

                if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)) {
                    roleDtos = roleService.getAllRoles()
                    .stream()
                    .map(entity -> mapper.map(entity, RoleDto.class))
                    .collect(Collectors.toList());
                }

                if (SecurityUtils.isUserHasRole(Role.ADMIN) && (!SecurityUtils.isUserHasRole(Role.SUPER_ADMIN))) {
                    roleDtos = roleService.getAllRoles()
                            .stream().filter(role -> role.getRole().equals(Role.CONTRIBUTOR) || role.getRole().equals(Role.USER) ||role.getRole().equals(Role.ADMIN))
                            .map(entity -> mapper.map(entity,RoleDto.class))
                            .collect(Collectors.toList());
                }

                comboRoles.setItems(roleDtos);


                comboRoles.addValueChangeListener(event-> {

                    Set<RoleDto> adminRoles =  event.getValue()
                            .stream()
                            .filter(item-> item.getRole().equals(Role.ADMIN))
                            .collect(Collectors.toSet());


                   if(adminRoles.size() > 0) {
                       accountComboBox.setVisible(true);
                         userBinder.forField(accountComboBox).asRequired("Account Is Required")
                        .bind(UserDto::getAccounts,UserDto::setAccounts);
                   } else {
                       accountComboBox.setVisible(false);
                        userBinder.removeBinding(accountComboBox);
                   }

                });


        populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
        userFormLayout.setVisible(false);
        btnAdd.setEnabled(false);
        btnAddUser.addClickListener(event -> {
            addUser();
        });

        btnAdd.addClickListener(event-> {
            if(userBinder.isValid()) {

                try{
                    User user = mapper.map(userBinder.getBean(),User.class);

                    List<Account> accounts;

                    boolean hasAdmin =  (comboRoles.getValue()
                            .stream()
                            .filter(item-> item.getRole().equals(Role.ADMIN))
                            .collect(Collectors.toSet()).size() >  0);


                    if(hasAdmin) {
                      accounts =  accountComboBox.getValue()
                                .stream()
                                .map(entity-> mapper.map(entity, Account.class))
                                .collect(Collectors.toList());

                    } else {
                      accounts =  (userService.findByEmail(SecurityUtils.getLoggedInUsername())).getAccounts()
                              .stream()
                              .map(entity-> mapper.map(entity,Account.class))
                              .collect(Collectors.toList());
                    }


                    if(btnAdd.getElement().getText().equalsIgnoreCase("save")){
                        try {

                            User savedUser = userService.save(user);

                            accounts.forEach(account -> {
                                account.getUsers().add(savedUser);
                                try {
                                    accountService.update(account);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            Notification.show("User Added Successfully");
                        } catch (Exception e) {
                            Notification.show("Some Error Occurred");
                        }

                    } else if(btnAdd.getElement().getText().equalsIgnoreCase("update")) {
                        try {
                            User savedUser = userService.update(user);

                            accounts.forEach(account -> {
                                account.getUsers().add(savedUser);
                                try {
                                    accountService.update(account);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });


                            Notification.show("User Updated Successfully");
                        } catch (Exception e) {
                            Notification.show("Some Error Occurred");
                        }
                    }

                    populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
                    clearUserForm();
                    userFormLayout.setVisible(false);
                } catch (Exception e) {
                    Notification.show("Some error occurred while saving the user");
                    e.printStackTrace();
                }
            }
        });

        btnCancel.addClickListener(event-> {
            userFormLayout.setVisible(false);
            clearUserForm();
        });


        txtSearch.addValueChangeListener(event -> {
            String textSearch = event.getValue();
            pagination.resetPaginationVariable(pagination.getDdPageSize());
                populateGridData(loadUsers(Optional.of(textSearch), pagination.currentPage, pagination.pageSize));
        });


        btnDownloadUsers.addClickListener(buttonClickEvent -> {
            final StreamResource resource = new StreamResource("users.csv", () -> new ByteArrayInputStream(createUsersCSV().getBytes()));
            resource.setContentType("application/vnd.ms-excel");
            final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
            UI.getCurrent().getPage().executeJavaScript("window.open($0, $1)", registration.getResourceUri().toString(), "_blank");
        });

        btnImportUser.addClickListener(buttonClickEvent -> importCsv());
    }

    private void clearUserForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPassword.clear();
        comboRoles.clear();
        userBinder.removeBean();
        txtPassword.setVisible(true);
    }

    private Button editUser(UserDto userDto) {

        userBinder.removeBinding(txtPassword);

        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        editButton.addClickListener(event-> {
            userFormLayout.setVisible(true);
            txtPassword.setVisible(false);
            btnAdd.setText("Update");
            userBinder.setBean(userDto);

        });
        return editButton;
    }


    private void addUser() {

        userBinder.forField(txtPassword).asRequired("Enter you password")
                .withValidator(new StringLengthValidator("Password length must be between 6 and 15", 6, 15))
                .bind(UserDto::getEmail, UserDto::setPasswordHash);

        btnAdd.setText("Save");
        userFormLayout.setVisible(true);

        UserDto userDto = new UserDto();
        userDto.setFirstName(txtFirstName.getValue());
        userDto.setLastName(txtLastName.getValue());
        userDto.setEmail(txtEmail.getValue());
        userDto.setPasswordHash(txtPassword.getValue());
        userDto.setRoles(comboRoles.getValue());
        userBinder.setBean(userDto);
    }

    private void initGrid() {

        if(!(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN) || SecurityUtils.isUserHasRole(Role.ADMIN))){
            btnAddUser.setVisible(false);
            btnImportUser.setVisible(false);
            btnDownloadUsers.setVisible(false);
            comboRoles.setReadOnly(true);
        }


        userGrid.addComponentColumn(this::editUser).setHeader("Actions").setAutoWidth(true);
        userGrid.addColumn(UserDto::getFirstName).setHeader("First Name").setAutoWidth(true);
        userGrid.addColumn(UserDto::getLastName).setHeader("Last Name").setAutoWidth(true);
        userGrid.addColumn(UserDto::getEmail).setHeader("Email").setAutoWidth(true);
        userGrid.addColumn(new ComponentRenderer<>(User-> {
            FlexLayout rolesLayout = new FlexLayout();
            rolesLayout.setWrapMode(FlexLayout.WrapMode.WRAP);
            User.getRoles().stream().forEach(role -> {
                Span badge = new Span(role.getRole());
                badge.getElement().setAttribute("theme","badge success");
                badge.getStyle().set("margin","3px");
                rolesLayout.add(badge);
            });
            return rolesLayout;
        })).setHeader("Roles").setAutoWidth(true);

        userGrid.addItemClickListener(event-> {
            userBinder.removeBinding(txtPassword);
            userFormLayout.setVisible(true);
            txtPassword.setVisible(false);
            btnAdd.setText("Update");
            userBinder.setBean(event.getItem());
        });
    }

    private void initBinder() {
        userBinder.forField(txtFirstName).asRequired("First Name is Required").bind(UserDto::getFirstName,UserDto::setFirstName);
        userBinder.forField(txtLastName).asRequired("Last Name is Required").bind(UserDto::getLastName,UserDto::setLastName);
        userBinder.forField(txtEmail).asRequired("Email is required")
                .withValidator(new EmailValidator("This doesn't look like a valid email address"))
                .bind(UserDto::getEmail,UserDto::setEmail);


        userBinder.forField(comboRoles).asRequired("Select one or more roles")
                .bind(UserDto::getRoles,UserDto::setRoles);

        userBinder.forField(accountComboBox).asRequired("Account Is Required")
                .bind(UserDto::getAccounts,UserDto::setAccounts);

        userBinder.addStatusChangeListener(event1-> {

            boolean isValid;

            isValid = event1.getBinder().isValid();

            if(isValid) {
                btnAdd.setEnabled(true);
            } else {
                btnAdd.setEnabled(false);
            }

        });
    }

    private void paginationClickListeners(PaginationComponent pagination) {

        pagination.getBtnFirstPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = 0;
             populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });

        pagination.getBtnLastPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = this.pagination.totalPages - 1;
                populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });

        pagination.getBtnNextPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = this.pagination.currentPage + 1;
                populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });
        pagination.getBtnPreviousPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = this.pagination.currentPage - 1;
                populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });

        pagination.getDdPageSize().addValueChangeListener(event -> {
            this.pagination.currentPage = 0;
            this.pagination.pageSize = event.getValue();

                populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
            txtSearch.setValue("");
        });
    }

    private void populateGridData(Page<User> users) {
        pagination.totalPages = users.getTotalPages();


        if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)) {
            List<UserDto> userDtos = users.getContent()
                    .stream()
                    .map(entity -> mapper.map(entity, UserDto.class))
                    .collect(Collectors.toList());
            userGrid.setItems(userDtos);
        }

        if(SecurityUtils.isUserHasRole(Role.ADMIN) && (!SecurityUtils.isUserHasRole(Role.SUPER_ADMIN))) {
            List<UserDto> userDtos= new ArrayList<>();

            users.getContent()
                    .forEach(user-> {
                        userService.findByEmail(SecurityUtils.getLoggedInUsername()).getAccounts()
                                .stream()
                                .forEach(account -> {
                                    user.getAccounts().forEach(currentUserAccount-> {
                                        if(account.getId().equals (currentUserAccount.getId())){
                                            userDtos.add(mapper.map(user,UserDto.class));
                                        }
                                    });
                                });
                    });

            userGrid.setItems(userDtos);

        }

        if(!(SecurityUtils.isUserHasRole(Role.ADMIN) || SecurityUtils.isUserHasRole(Role.SUPER_ADMIN))) {
            List<UserDto> userDtos = users.getContent()
                    .stream()
                    .filter(item-> (userService.findByEmail(SecurityUtils.getLoggedInUsername())).getEmail().equals(item.getEmail()))
                    .map(entity -> mapper.map(entity, UserDto.class))
                    .collect(Collectors.toList());
            userGrid.setItems(userDtos);
        }

        pagination.setPaginationButtonsState();
    }

    private Page<User> loadUsers(Optional<String> filter, int page, int size) {
        return userService.findAnyMatching(filter, PageRequest.of(page, size));
    }

    private String createUsersCSV() {
        StringBuilder output = new StringBuilder("Email,First Name,Last Name,Roles\n");

        try {
            List<UserDto> users;
            users = userService.getAllUsers()
                    .stream()
                    .map(user-> mapper.map(user,UserDto.class))
                    .collect(Collectors.toList());


            if (users != null) {
                users.forEach(user -> {
                    output.append(user.getEmail()).append(",");
                    output.append(user.getFirstName()).append(",");
                    output.append(user.getLastName()).append(",");
                    user.getRoles().stream().findFirst().ifPresent(role -> output.append(role.getRole()));
                    user.getRoles().stream().skip(1).forEach(role -> {
                        output.append(":").append(role.getRole());
                    });

                    output.append("\n");
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return output.toString();
    }

    private void importCsv() {
        Dialog csvUploadDialog = new Dialog();
        csvUploadDialog.add(new CSVUploadView(new IAction() {
            @Override
            public void save() {
                if (userGrid != null) {
                    populateGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize));
                }
                csvUploadDialog.close();
            }

            @Override
            public void cancel() {
                csvUploadDialog.close();
            }
        }, userService));

        csvUploadDialog.open();
    }


}
