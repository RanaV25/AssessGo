package com.assessgo.frontend.views.login;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.assessgo.backend.DummyData;
import com.assessgo.backend.dto.AccountDto;
import com.assessgo.backend.dto.UserDto;
import com.assessgo.backend.entity.Account;
import com.assessgo.backend.entity.Role;
import com.assessgo.backend.entity.User;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.AccountService;
import com.assessgo.backend.service.RoleService;
import com.assessgo.backend.service.UserService;
import com.assessgo.frontend.views.dashboard.DashboardView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Tag("login-view")
@JsModule("./src/views/login/login-view.js")
@Route("login")
public class LoginView extends PolymerTemplate<LoginView.LoginViewModel> implements AfterNavigationObserver, BeforeEnterObserver, PageConfigurator {

    @Id("userLoginForm")
    private LoginForm userLoginForm;

    @Id("btnSignUp")
    private Button btnSignUp;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private RoleService roleService;

    private byte[] bytes;
    private Image imageDisplay;

    private Dialog dialogSignUp;
    private Dialog addAccount;


    private String imageBasePath = "uploadedImages";


    public LoginView() {
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

        userLoginForm.setAction("login");

        if(afterNavigationEvent.getLocation().getQueryParameters().getParameters().containsKey(
                "error")) {
            userLoginForm.setError(true);
        }


        btnSignUp.addClickListener(event -> {
            createSignUpDialogView();
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

        if (SecurityUtils.isUserLoggedIn()) {
            UI.getCurrent().getPage().getHistory().replaceState(null, "");
            beforeEnterEvent.rerouteTo(DashboardView.class);
        }

    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addInlineWithContents(InitialPageSettings.Position.PREPEND,
                "window.customElements=window.customElements||{};" +
                        "window.customElements.forcePolyfill=true;" +
                        "window.ShadyDOM={force:true};", InitialPageSettings.WrapMode.JAVASCRIPT);
    }



    public interface LoginViewModel extends TemplateModel {

    }

    public void createSignUpDialogView() {

        Binder<UserDto> userDtoBinder = new Binder<>(UserDto.class);
        Binder<AccountDto> accountDtoBinder = new Binder<>(AccountDto.class);

        userDtoBinder.setBean(new UserDto());
        accountDtoBinder.setBean(new AccountDto());

        dialogSignUp = new Dialog();
        dialogSignUp.addDialogCloseActionListener(e-> {
           dialogSignUp.close();
        });

        dialogSignUp.setWidth("500px");
        dialogSignUp.setCloseOnEsc(false);
        dialogSignUp.setCloseOnOutsideClick(false);
        dialogSignUp.open();

        VerticalLayout signUpFormLayout = new VerticalLayout();
        signUpFormLayout.setWidthFull();
        signUpFormLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 title = new H3("Sign Up");

        TextField firstName = new TextField();
        firstName.setValueChangeMode(ValueChangeMode.EAGER);
        firstName.setLabel("Fist Name");
        firstName.setRequired(true);
        firstName.setWidthFull();
        userDtoBinder.forField(firstName).asRequired("First Name is required").bind(UserDto::getFirstName,UserDto::setFirstName);

        TextField lastName = new TextField();
        lastName.setValueChangeMode(ValueChangeMode.EAGER);
        lastName.setLabel("Last Name");
        lastName.setRequired(true);
        lastName.setWidthFull();
        userDtoBinder.forField(lastName).asRequired("Last Name is required").bind(UserDto::getLastName,UserDto::setLastName);

        TextField email = new TextField();
        email.setValueChangeMode(ValueChangeMode.EAGER);
        email.setLabel("Email");
        email.setRequired(true);
        email.setWidthFull();
        userDtoBinder.forField(email).asRequired("Email is required")
                .withValidator(new EmailValidator("This doesn't look like a valid email address"))
                .bind(UserDto::getEmail,UserDto::setEmail);


        PasswordField password = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.EAGER);
        password.setLabel("Password");
        password.setWidthFull();
        password.setRequired(true);
        userDtoBinder.forField(password).asRequired("Enter you password")
                .withValidator(new StringLengthValidator("Password length must be between 6 and 15", 6, 15))
                .bind(UserDto::getPasswordHash, UserDto::setPasswordHash);

        HorizontalLayout signUpButtonsLayout = new HorizontalLayout();
        signUpButtonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);



        Button btnCancel = new Button("Cancel");
        btnCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnNext = new Button("Next");
        btnNext.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNext.setEnabled(false);

        userDtoBinder.addStatusChangeListener(statusChangeEvent -> {
           if(userDtoBinder.isValid()) {
               btnNext.setEnabled(true);
           } else {
               btnNext.setEnabled(false);
           }
        });

        signUpButtonsLayout.add(btnCancel,btnNext);
        signUpFormLayout.add(title,firstName,lastName,email,password,signUpButtonsLayout);

        dialogSignUp.add(signUpFormLayout);

        btnNext.addClickListener(e-> {
            dialogSignUp.close();

            addAccount = new Dialog();
            addAccount.setWidth("300px");
            addAccount.setCloseOnEsc(false);
            addAccount.setCloseOnOutsideClick(false);
            addAccount.open();

            VerticalLayout addAccountContainer = new VerticalLayout();
            addAccountContainer.setWidthFull();
            addAccountContainer.setAlignItems(FlexComponent.Alignment.CENTER);

            H3 addAccountTitle = new H3("Add Account");

            imageDisplay = DummyData.getLogo();

            Upload uplAccountImage = new Upload();
            uplAccountImage.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif", "image/jpg");
            MemoryBuffer memoryBuffer = new MemoryBuffer();
            uplAccountImage.setReceiver(memoryBuffer);


            uplAccountImage.addFinishedListener(event -> {
                InputStream inputStream = memoryBuffer.getInputStream();
                try {
                    bytes = StreamUtils.copyToByteArray(inputStream);
                    StreamResource streamResource = new StreamResource(event.getFileName(),
                            () -> new ByteArrayInputStream(bytes));
                    imageDisplay.setSrc(streamResource);
                } catch (IOException e1) {
                    Notification notification = new Notification(e1.getMessage());
                    notification.open();
                    e1.printStackTrace();
                    return;
                }
            });

            uplAccountImage.getElement().addEventListener("file-remove", new DomEventListener() {
                @Override
                public void handleEvent(DomEvent arg0) {
                    imageDisplay.setSrc(DummyData.getImageSource());
                    bytes = null;
                }
            });


            VerticalLayout imageLayout = new VerticalLayout(imageDisplay, uplAccountImage);
            imageLayout.setAlignItems(FlexComponent.Alignment.CENTER);



            TextField accountName = new TextField("Account Name");
            accountName.setValueChangeMode(ValueChangeMode.EAGER);
            accountName.setRequired(true);
            accountName.setWidthFull();
            accountDtoBinder.forField(accountName)
                    .asRequired("Account Name is Required")
                    .bind(AccountDto::getAccountName,AccountDto::setAccountName);

            HorizontalLayout buttonsLayout = new HorizontalLayout();
            buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            Button btnCancelAccount = new Button("Cancel");
            btnCancelAccount.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button btnSaveAccount = new Button("Save");
            btnSaveAccount.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            btnSaveAccount.setEnabled(false);

            buttonsLayout.add(btnSaveAccount,btnCancelAccount);
            addAccountContainer.add(addAccountTitle,imageLayout,accountName,buttonsLayout);
            addAccount.add(addAccountContainer);


            accountDtoBinder.addStatusChangeListener(statusChangeEvent -> {
               if(accountDtoBinder.isValid()) {
                   btnSaveAccount.setEnabled(true);
               } else {
                   btnCancelAccount.setEnabled(false);
               }
            });

            btnSaveAccount.addClickListener(saveEvent -> {
                if(userDtoBinder.isValid() && accountDtoBinder.isValid()) {

                    try {
                        User user = mapper.map(userDtoBinder.getBean(),User.class);
                       Set<Role> roles = roleService.getAllRoles()
                                .stream()
                                .filter(role-> role.getRole().equals(com.assessgo.backend.common.Role.ADMIN))
                                .collect(Collectors.toSet());
                       user.setRoles(roles);

                        userService.save(user);

                        Account account = mapper.map(accountDtoBinder.getBean(),Account.class);
                        Set<User> users = new HashSet<>();
                        users.add(user);
                        account.setUsers(users);
                        account.setProfileImage(getAttachedAvatarPath(accountDtoBinder.getBean().getAccountName(),bytes,imageDisplay));
                        accountService.save(account);

                        Notification.show("Sign process finish. Kindly use your username and password to login.");

                        addAccount.close();

                        UI.getCurrent().navigate(LoginView.class);

                    } catch (Exception ex) {
                        Notification.show(ex.getMessage());
                        ex.printStackTrace();
                    }


                }
            });

            btnCancelAccount.addClickListener(cancelEvent-> {
                addAccount.close();
                accountDtoBinder.removeBean();
                userDtoBinder.removeBean();
            });


        });

        btnCancel.addClickListener(e-> {
           dialogSignUp.close();
           accountDtoBinder.removeBean();
           userDtoBinder.removeBean();
        });



    }

    public String getAttachedAvatarPath(final String accountName, byte[] bytes, Image imageDisplay) throws IOException {
        String currentFileName;
        String imagesBasePath = imageBasePath;
        currentFileName = accountName + "_" + System.currentTimeMillis() + ".png";

        File directory = new File(imagesBasePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File targetFile = new File(imagesBasePath + File.separator + currentFileName);

        if (bytes == null || bytes.length < 1) {
            String imageUrl = VaadinServletService.getCurrentServletRequest().getRequestURL().toString() + imageDisplay.getSrc();
            bytes = IOUtils.toByteArray(new URL(imageUrl));
        }
        FileUtils.writeByteArrayToFile(targetFile, bytes);
        return currentFileName;
    }
}
