package com.assessgo.frontend.views.businessgroups;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import com.assessgo.MainView;
import com.assessgo.backend.dto.BusinessGroupDto;
import com.assessgo.backend.dto.GraphModel;
import com.assessgo.backend.entity.BusinessGroup;
import com.assessgo.backend.service.BusinessGroupService;
import com.assessgo.frontend.model.BusinessGroupsViewModel;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "add-business-groups", layout = MainView.class)
@RouteAlias(value = "add-business-groups", layout = MainView.class)
@PageTitle("Add Business Groups")
@NpmPackage(value = "gojs", version = "2.1.4")
@JsModule("src/views/business-groups/add-business-groups-views.js")
@Tag("add-business-groups-view")
public class AddBusinessGroupsView extends PolymerTemplate<BusinessGroupsViewModel> implements AfterNavigationObserver {

    public static final Logger LOGGER = LoggerFactory.getLogger(AddBusinessGroupsView.class);

    @Id("txtName")
    private TextField name;

    @Id("txtDescription")
    private TextArea desc;

    @Autowired
    private ModelMapper mapper;

    @Id("btnAdd")
    private Button btnAdd;

    @Autowired
    private BusinessGroupService businessGroupService;

    private Binder<BusinessGroupDto> bgBinder = new Binder<>();
    private BusinessGroupDto businessGroupBean;

    public AddBusinessGroupsView() {
        initBinder();
        btnAdd.setEnabled(false);
    }

    private void initBinder() {
        bgBinder.forField(name).asRequired("name is Required").bind(BusinessGroupDto::getName, BusinessGroupDto::setName);
        bgBinder.forField(desc).bind(BusinessGroupDto::getDescription, BusinessGroupDto::setDescription);

        bgBinder.addStatusChangeListener(evt -> {

            if (evt.getBinder().isValid()) {
                btnAdd.setEnabled(true);
            } else {
                btnAdd.setEnabled(false);
            }

        });
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        ObjectMapper om = new ObjectMapper();
        try {
            getModel().setData(om.writeValueAsString(map()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        btnAdd.addClickListener(event -> {
            try {
                if (bgBinder.isValid()) {
                    bgBinder.writeBean(this.businessGroupBean);

                    if (this.businessGroupBean.getStatus() == GraphModel.NEW) {
                        BusinessGroup businessGroup = mapper.map(this.businessGroupBean, BusinessGroup.class);
                        BusinessGroup parent = businessGroupService.load(this.businessGroupBean.getParent());
                        businessGroup.setBusinessGroup(parent);

                        businessGroupService.save(businessGroup);
                        Notification.show("Business group added successfully");
                    } else {
                        BusinessGroup businessGroup = businessGroupService.load(this.businessGroupBean.getId());
                        mapper.map(this.businessGroupBean, businessGroup);
                        businessGroupService.update(businessGroup);
                        Notification.show("Business group updated successfully");
                    }

                    clearForm();

                    try {
                        getModel().setData(om.writeValueAsString(map()));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Notification.show("Some error occurred while saving the business group");
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    private void clearForm() {
        this.name.clear();
        this.desc.clear();
    }

    @ClientCallable
    private void setData(String json) {
        try {
            ObjectMapper om = new ObjectMapper();
            this.businessGroupBean = om.readValue(json, BusinessGroupDto.class);
            bgBinder.readBean(this.businessGroupBean);

            if(this.businessGroupBean.getStatus() == GraphModel.DELETED)
            {
                ConfirmDialog dialog = new ConfirmDialog("Confirm delete",
                        "Are you sure you want to delete the business group?",
                        "Delete", this::onDelete, "Cancel", this::onCancel);
                dialog.setConfirmButtonTheme("error primary");
                dialog.open();
            }
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void onCancel(ConfirmDialog.CancelEvent cancelEvent) {
    }

    private void onDelete(ConfirmDialog.ConfirmEvent event) {
        businessGroupService.delete(this.businessGroupBean.getId());

        clearForm();

        try {
            ObjectMapper om = new ObjectMapper();
            getModel().setData(om.writeValueAsString(map()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
        return null;
    }

    @Override
    public Registration addDetachListener(ComponentEventListener<DetachEvent> listener) {
        return null;
    }

    private JsonArray map() {
        JsonArray ja = Json.createArray();
        this.businessGroupService.findAll().forEach(bg -> {
            JsonObject jo = Json.createObject();
            jo.put("id", bg.getId());
            jo.put("name", bg.getName());
            jo.put("description", bg.getDescription());
            jo.put("status", bg.getStatus());

            if (bg.getParent() != null) {
                jo.put("parent", bg.getParent());
            }
            ja.set(ja.length(), jo);
        });

        return ja;
    }
}
