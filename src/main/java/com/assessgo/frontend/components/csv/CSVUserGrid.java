package com.assessgo.frontend.components.csv;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.assessgo.backend.dto.UserDto;
import com.assessgo.backend.enums.UserValidation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CSVUserGrid extends Composite<VerticalLayout> {

    private final Grid<UserDto> grid;
    private final Button save;
    private final Button cancel;
    private final Button delete;

    private List<UserDto> users;

    public CSVUserGrid() {
        grid = new Grid<>();
        save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel = new Button("Cancel");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete = new Button("Delete");
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.setEnabled(false);
        getContent().add(createUsersGrid(), createFooter());
    }

    private Component createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        FlexLayout btnPanel1 = new FlexLayout();
        FlexLayout btnPanel2 = new FlexLayout();
        footer.add(btnPanel1);
        footer.add(btnPanel2);
        footer.setWidth("100%");
        btnPanel1.setWidth("80%");
        btnPanel2.setWidth("20%");
        btnPanel1.add(save, cancel);
        btnPanel2.add(delete);
        return footer;
    }

    private Component createUsersGrid() {
        grid.setHeightByRows(true);

        Binder<UserDto> binder = new Binder<>(UserDto.class);
        grid.getEditor().setBinder(binder);

        TextField txtFirstName = new TextField();
        grid.addColumn(new ComponentRenderer<>(userDto -> {
            if (userDto.getValidationCode() == UserValidation.FIRST_NAME_REQUIRED) {
                Html div = new Html("<div title='First name required'>" + VaadinIcon.WARNING.create().getElement().getOuterHTML() + userDto.getFirstName() + "</div>");
                div.getElement().getStyle().set("color", "red");
                return div;
            } else {
                Div div = new Div();
                div.setText(userDto.getFirstName());
                return div;
            }
        })).setHeader("First Name").setAutoWidth(true).setEditorComponent(txtFirstName);

        binder.forField(txtFirstName).bind("firstName");

        TextField txtLastName = new TextField();
        grid.addColumn(new ComponentRenderer<>(userDto -> {
            if (userDto.getValidationCode() == UserValidation.LAST_NAME_REQUIRED) {
                Html div = new Html("<div title='Last name required'>" + VaadinIcon.WARNING.create().getElement().getOuterHTML() + userDto.getLastName() + "</div>");
                div.getElement().getStyle().set("color", "red");
                return div;
            } else {
                Div div = new Div();
                div.setText(userDto.getPasswordHash());
                return div;
            }
        })).setHeader("Last Name").setAutoWidth(true).setEditorComponent(txtLastName);

        binder.forField(txtLastName).bind("lastName");

        TextField txtEmail = new TextField();
        grid.addColumn(new ComponentRenderer<>(userDto -> {
            if (userDto.getValidationCode() == UserValidation.INVALID_EMAIL) {
                Html div = new Html("<div title='Invalid email'>" + VaadinIcon.WARNING.create().getElement().getOuterHTML() + userDto.getEmail() + "</div>");
                div.getElement().getStyle().set("color", "red");
                return div;
            } else if (userDto.getValidationCode() == UserValidation.USERNAME_ALREADY_EXISTS) {
                Html div = new Html("<div title='This user already exists'>" + VaadinIcon.WARNING.create().getElement().getOuterHTML() + userDto.getEmail() + "</div>");
                div.getElement().getStyle().set("color", "red");
                return div;
            } else {
                Div div = new Div();
                div.setText(userDto.getEmail());
                return div;
            }
        })).setHeader("Email").setAutoWidth(true).setEditorComponent(txtEmail);

        binder.forField(txtEmail).bind("email");

        TextField txtPassword = new TextField();
        grid.addColumn(new ComponentRenderer<>(userDto -> {
            if (userDto.getValidationCode() == UserValidation.PASSWORD_NOT_FOUND || userDto.getValidationCode() == UserValidation.NEED_A_STRONG_PASSWORD) {
                Html div = new Html("<div title='Strong password needed'>" + VaadinIcon.WARNING.create().getElement().getOuterHTML() + userDto.getPasswordHash() + "</div>");
                div.getElement().getStyle().set("color", "red");
                return div;
            } else {
                Div div = new Div();
                div.setText(userDto.getPasswordHash());
                return div;
            }
        })).setHeader("Password").setAutoWidth(true).setEditorComponent(txtPassword);

        binder.forField(txtPassword).bind("passwordHash");

        grid.addColumn(new ComponentRenderer<>(userDto -> {
            FlexLayout rolesLayout = new FlexLayout();
            rolesLayout.setWrapMode(FlexLayout.WrapMode.WRAP);
            userDto.getRoles().stream().forEach(roleDto -> {

                Span badge = new Span(roleDto.getRole());
                badge.getElement().setAttribute("theme","badge success");
                badge.addClassNames("badge","success");
                badge.getStyle().set("margin","3px");
                rolesLayout.add(badge);


            });
            return rolesLayout;
        })).setHeader("Roles").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("800px");


        grid.addItemDoubleClickListener(event -> {
            grid.getEditor().editItem(event.getItem());
            txtFirstName.focus();
        });

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(selectionEvent -> delete.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        return grid;
    }

    public List<UserDto> getSelectedItems() {
        Set<UserDto> selectedSet = this.grid.getSelectedItems();
        return new ArrayList<>(selectedSet);
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        grid.setItems(users);
        this.users = users;
    }

    public void addSaveListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.save.addClickListener(listener);
    }

    public void addCancelListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.cancel.addClickListener(listener);
    }

    public void addDeleteListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.delete.addClickListener(listener);
    }
}
