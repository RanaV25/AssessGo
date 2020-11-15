package com.assessgo.frontend.views.requirement;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.assessgo.MainView;
import com.assessgo.backend.entity.Characteristic;
import com.assessgo.backend.entity.Objective;
import com.assessgo.backend.entity.Requirement;
import com.assessgo.backend.enums.RequirementTypeEnum;
import com.assessgo.backend.service.CharacteristicService;
import com.assessgo.backend.service.ObjectiveService;
import com.assessgo.backend.service.RequirementService;
import com.assessgo.frontend.components.navigation.bar.AppBar;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Tag("requirement-view")
@JsModule("./src/views/requirement/requirement-view.js")
@JsModule("./styles/shared-styles.js")
@CssImport("styles/views/requirement/requirement.css")
@Route(value="requirement", layout = MainView.class)
public class RequirementView extends PolymerTemplate<RequirementView.RequirementViewModel> implements AfterNavigationObserver {


    @Id("mainContainer")
    private VerticalLayout mainContainer;
    @Id("horizontalScrollView")
    private HorizontalLayout horizontalScrollView;
    @Id("detailsContainer")
    private VerticalLayout detailsContainer;
    @Id("viewSwapperContainer")
    private VerticalLayout viewSwapperContainer;
    @Id("btnViewSwapper")
    private Button btnViewSwapper;
    @Id("spreadSheetContainer")
    private VerticalLayout spreadSheetContainer;
    @Id("detailsCard")
    private VerticalLayout detailsCard;
    @Id("requirementName")
    private TextField requirementName;
    @Id("requirementDescription")
    private TextArea requirementDescription;
    @Id("requirementType")
    private Select<String> requirementType;
    @Id("requirementLastEditedDate")
    private TextField requirementLastEditedDate;
    @Id("strategyContainer")
    private VerticalLayout strategyContainer;
    @Id("stakeholderContainer")
    private VerticalLayout stakeholderContainer;
    @Id("objectivesContainer")
    private VerticalLayout objectivesContainer;
    @Id("characteristicsContainer")
    private VerticalLayout characteristicsContainer;
    @Id("characteristicsGrid")
    private Grid<Characteristic> characteristicsGrid;
    @Id("objectivesGrid")
    private Grid<Objective> objectivesGrid;

    private Tabs tabs;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private ObjectiveService objectiveService;

    @Autowired
    private CharacteristicService characteristicService;

    Binder<Requirement> requirementBinder = new Binder<>();
    @Id("stakeholderContentContainer")
    private VerticalLayout stakeholderContentContainer;
    @Id("strategyContentContainer")
    private VerticalLayout strategyContentContainer;
    @Id("btnAddStrategy")
    private Button btnAddStrategy;
    @Id("btnAddStakeholder")
    private Button btnAddStakeholder;
    @Id("btnAddCharacteristics")
    private Button btnAddCharacteristics;
    @Id("btnAddObjective")
    private Button btnAddObjective;

    Grid<Requirement> requirementGrid = new Grid<>();


    public RequirementView() {
        setId("requirementView");
        initView();
        initGrids();
        initBinder();
        addButtonClickListeners();
        initListView();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        initAppBar();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        addContents();
        autoSave();
        populateRequirementGrid();
    }


    public interface RequirementViewModel extends TemplateModel {

    }

    private AppBar initAppBar() {
        AppBar appBar = MainView.get().getAppBar();
        appBar.setTitle("Requirement");
        return appBar;
    }

    public void addContents() {

        requirementService.getRepository().findAll().stream()
                .forEach(requirement -> {
                    Tab tab = new Tab();
                    tab.addClassNames("scrolling-tab");

                    tab.setId(requirement.getId().toString());
                    tab.removeAll();
                    tab.add(addUpdateHorizontalCardInfo(requirement));

                    tabs.add(tab);
                });


        horizontalScrollView.add(tabs);


        btnViewSwapper.addClickListener(event-> {
            if(btnViewSwapper.getElement().getText().equalsIgnoreCase("list view")) {
                horizontalScrollView.setVisible(false);
                detailsContainer.setVisible(false);
                spreadSheetContainer.setVisible(true);
                btnViewSwapper.setText("Card View");
            } else {
                horizontalScrollView.setVisible(true);
                detailsContainer.setVisible(true);
                spreadSheetContainer.setVisible(false);
                btnViewSwapper.setText("List View");
            }
        });
    }

    private void initView() {
        requirementType.setItems(RequirementTypeEnum.REQUIREMENT_TYPE_1.getValue(),RequirementTypeEnum.REQUIREMENT_TYPE_2.getValue(),
                RequirementTypeEnum.REQUIREMENT_TYPE_3.getValue());
        btnViewSwapper.setText("List View");
        spreadSheetContainer.setVisible(false);
        spreadSheetContainer.add(new H3("Requirements"));

        tabs = new Tabs();
        tabs.addClassName("scrolling-tabs");
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(event-> {
           changeCardDetailsOnClick(tabs.getSelectedTab().getId().get().toString());

        });

    }


    private void initGrids() {
        Grid.Column<Characteristic> nameColumn = characteristicsGrid.addColumn(Characteristic::getName).setHeader("Name");
        Grid.Column<Characteristic> valueColumn = characteristicsGrid.addColumn(Characteristic::getValue).setHeader("Value");
        Binder<Characteristic> characteristicBinder = new Binder<>(Characteristic.class);
        characteristicsGrid.getEditor().setBinder(characteristicBinder);

        TextField txtName = new TextField();
        TextField txtValue = new TextField();

        txtName.getElement()
                .addEventListener("keydown",
                        event -> characteristicsGrid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");

        characteristicBinder.forField(txtName)
                .asRequired("Name is required")
                .bind("name");
        nameColumn.setEditorComponent(txtName);

        txtValue.getElement()
                .addEventListener("keydown",
                        event -> characteristicsGrid.getEditor().cancel())
                .setFilter("event.key === 'Tab'");
        characteristicBinder.forField(txtValue)
                .asRequired("Value is Required")
                .bind("value");
        valueColumn.setEditorComponent(txtValue);

        characteristicsGrid.addItemDoubleClickListener(event -> {
            characteristicsGrid.getEditor().editItem(event.getItem());
            txtName.focus();
        });

        characteristicsGrid.getEditor().addCloseListener(event -> {
            if (characteristicBinder.getBean() != null) {
                try {
                    characteristicService.update(characteristicBinder.getBean());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        Grid.Column<Objective> nameObjectiveColumn = objectivesGrid.addColumn(Objective::getName).setHeader("Name");
        Grid.Column<Objective> descriptionObjectiveColumn = objectivesGrid.addColumn(Objective::getDescription).setHeader("Description");
        Binder<Objective> objectiveBinder = new Binder<>(Objective.class);
        objectivesGrid.getEditor().setBinder(objectiveBinder);

        TextField txtObjectiveName = new TextField();
        TextArea txtObjectiveDescription = new TextArea();

        txtObjectiveName.getElement()
                .addEventListener("keydown",
                        event -> objectivesGrid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");

        objectiveBinder.forField(txtObjectiveName)
                .asRequired("Name is required")
                .bind("name");
        nameObjectiveColumn.setEditorComponent(txtObjectiveName);

        txtObjectiveDescription.getElement()
                .addEventListener("keydown",
                        event -> objectivesGrid.getEditor().cancel())
                .setFilter("event.key === 'Tab'");
        objectiveBinder.forField(txtObjectiveDescription)
                .asRequired("Description is Required")
                .bind("description");
        descriptionObjectiveColumn.setEditorComponent(txtObjectiveDescription);

        objectivesGrid.addItemDoubleClickListener(event -> {
            objectivesGrid.getEditor().editItem(event.getItem());
            txtObjectiveName.focus();
        });

        objectivesGrid.getEditor().addCloseListener(event -> {
            if (objectiveBinder.getBean() != null) {
                try {
                    objectiveService.update(objectiveBinder.getBean());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeCardDetailsOnClick(String id) {
        Long requirementId = Long.valueOf(id);
        Optional<Requirement> requirement = requirementService.getRepository().findById(requirementId);
        requirement.ifPresent(requirement1 -> {
            requirementBinder.setBean(requirement1);
            characteristicsGrid.setItems(requirement1.getCharacteristics());
            objectivesGrid.setItems(requirement1.getObjectives());

            populateStrategyContainer(requirement1);

            populateStakeholdersContainer(requirement1);



//            stakeholderContentContainer.removeAll();
//            requirement1.getStakeholders().forEach(stakeholder->{
//                Span span = new Span(stakeholder);
//                span.getStyle().set("border-bottom","1px solid #D3D8E0");
//                span.setWidthFull();
//                span.addClassName("row-span");
//                stakeholderContentContainer.add(span);
//
//                span.addClickListener(event-> {
//                    Dialog dialog = new Dialog();
//                    VerticalLayout mainLayout = new VerticalLayout();
//                    mainLayout.add(new H3("Update Stakeholder"));
//                    mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//                    TextField txtStakeholder = new TextField();
//                    txtStakeholder.setValue(span.getText());
//                    HorizontalLayout buttonsLayout = new HorizontalLayout();
//                    Button btnUpdate = new Button("Update");
//                    Button btnCancel = new Button("Cancel");
//                    buttonsLayout.add(btnUpdate,btnCancel);
//                    mainLayout.add(txtStakeholder,buttonsLayout);
//                    dialog.add(mainLayout);
//                    dialog.open();
//                    btnUpdate.addClickListener(event1-> {
//                        requirement1.getStakeholders().remove(stakeholder);
//                        requirement1.getStakeholders().add(txtStakeholder.getValue());
//                        try {
//                            requirementService.update(requirement1);
//                            stakeholderContentContainer.remove(span);
//                            Span span1 = new Span(txtStakeholder.getValue());
//                            span1.addClassName("row-span");
//                            span1.setWidthFull();
//                            span1.getStyle().set("border-bottom","1px solid #D3D8E0");
//                            stakeholderContentContainer.add(span1);
//                            dialog.close();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    });
//
//                    btnCancel.addClickListener(event1-> {
//                        dialog.close();
//                    });
//                });
//            });
        });
    }

    private void initBinder() {
        requirementBinder.forField(requirementName).asRequired()
                .bind(Requirement::getName,Requirement::setName);

        requirementBinder.forField(requirementDescription).asRequired()
                .bind(Requirement::getRequirementDescription,Requirement::setRequirementDescription);

        requirementBinder.forField(requirementType).bind(Requirement::getType,Requirement::setType);

        requirementBinder.forField(requirementLastEditedDate)
                .bind(Requirement::getLastEdited,Requirement::setLastEdited);

    }

    private void autoSave() {
        requirementName.addBlurListener(event-> {
           Requirement tempRequirement = requirementBinder.getBean();
           tempRequirement.setName(requirementName.getValue());
           if(requirementBinder.isValid()) {
               try {
                   Requirement savedRequirement = requirementService.update(tempRequirement);
                   requirementLastEditedDate.setValue(savedRequirement.getLastEdited());
                   tabs.getSelectedTab().removeAll();
                   tabs.getSelectedTab().add(addUpdateHorizontalCardInfo(savedRequirement));
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
        });

        requirementDescription.addBlurListener(textAreaBlurEvent -> {
            Requirement tempRequirement = requirementBinder.getBean();
            tempRequirement.setRequirementDescription(requirementDescription.getValue());
            if(requirementBinder.isValid()) {
                try {
                    Requirement savedRequirement = requirementService.update(tempRequirement);
                    requirementLastEditedDate.setValue(savedRequirement.getLastEdited());
                    tabs.getSelectedTab().removeAll();
                    tabs.getSelectedTab().add(addUpdateHorizontalCardInfo(savedRequirement));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        requirementType.addValueChangeListener(event-> {
            Requirement tempRequirement = requirementBinder.getBean();
            tempRequirement.setType(requirementType.getValue());
            if(requirementBinder.isValid()) {
                try {
                    requirementLastEditedDate.setValue(requirementService.update(tempRequirement).getLastEdited());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public VerticalLayout addUpdateHorizontalCardInfo(Requirement requirement) {
        VerticalLayout div = new VerticalLayout();
        div.setWidth("150px");
        div.setHeight("150px");
        div.addClassName("scrollingCard");
        div.getStyle().set("box-shadow","0 4px 8px 0 rgba(0,0,0,0.2)");

        Span header = new Span(requirement.getName());
        header.getStyle().set("font-weight","bold");

        Span description = new Span(requirement.getRequirementDescription());

        div.add(header,description);
        return  div;
    }


    public void addButtonClickListeners() {
        btnAddStrategy.addClickListener(event-> {
            addUpdateStrategy();
        });

        btnAddStakeholder.addClickListener(event-> {
           addUpdateStakeholder();
        });

        btnAddCharacteristics.addClickListener(event-> {
           addUpdateCharacteristic();
        });

        btnAddObjective.addClickListener(event-> {
           addUpdateObjective();
        });
    }

    public void addUpdateStrategy() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(new H3("Add Strategy And Plan"));
        TextArea txtStrategy = new TextArea();
        mainLayout.add(txtStrategy);
        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setWidthFull();
        Button btnAdd = new Button("Add");
        Button btnCancel = new Button("Cancel");
        buttonsContainer.add(btnAdd,btnCancel);
        buttonsContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(buttonsContainer);
        dialog.add(mainLayout);

        dialog.open();

        btnAdd.addClickListener(event-> {
            if(txtStrategy.isEmpty()) {
                Notification.show("Fill All Fields First");
                return;
            }
           Requirement currentRequirement = requirementService.getRepository().findById(Long.valueOf(tabs.getSelectedTab().getId().get())).get();
           currentRequirement.getStrategyAndPlan().add(txtStrategy.getValue());
            try {
                Requirement savedRequirement = requirementService.update(currentRequirement);
                savedRequirement.getStrategyAndPlan().forEach(strategy->{
                    populateStrategyContainer(savedRequirement);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            txtStrategy.clear();
            dialog.close();
        });

        btnCancel.addClickListener(event-> {
            txtStrategy.clear();
            dialog.close();
        });

    }



    public void addUpdateStakeholder() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(new H3("Add Stakeholder"));
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        TextField txtStakeholder = new TextField();
        mainLayout.add(txtStakeholder);
        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setWidthFull();
        Button btnAdd = new Button("Add");
        Button btnCancel = new Button("Cancel");
        buttonsContainer.add(btnAdd,btnCancel);
        buttonsContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(buttonsContainer);
        dialog.add(mainLayout);

        dialog.open();

        btnAdd.addClickListener(event-> {
            if(txtStakeholder.isEmpty()) {
                Notification.show("Fill All Fields First");
                return;
            }
            Requirement currentRequirement = requirementService.getRepository().findById(Long.valueOf(tabs.getSelectedTab().getId().get())).get();
            currentRequirement.getStakeholders().add(txtStakeholder.getValue());
            try {
                Requirement savedRequirement = requirementService.update(currentRequirement);
//                stakeholderContentContainer.removeAll();
                savedRequirement.getStakeholders().forEach(stakeholder->{
//                    Span span = new Span(stakeholder);
//                    span.addClassName("row-span");
//                    span.setWidthFull();
//                    span.getStyle().set("border-bottom","1px solid #D3D8E0");
//                    stakeholderContentContainer.add(span);
                    populateStakeholdersContainer(savedRequirement);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            txtStakeholder.clear();
            dialog.close();
        });

        btnCancel.addClickListener(event-> {
            txtStakeholder.clear();
            dialog.close();
        });

    }

    public void addUpdateCharacteristic() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(new H3("Add Characteristics"));
        TextField txtName = new TextField();
        txtName.setLabel("Name");
        TextField txtValue = new TextField();
        txtValue.setLabel("Value");
        mainLayout.add(txtName);
        mainLayout.add(txtValue);
        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setWidthFull();
        Button btnAdd = new Button("Add");
        Button btnCancel = new Button("Cancel");
        buttonsContainer.add(btnAdd,btnCancel);
        buttonsContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(buttonsContainer);
        dialog.add(mainLayout);

        dialog.open();

        btnAdd.addClickListener(event-> {
            if(txtName.isEmpty() || txtValue.isEmpty()) {
                Notification.show("Fill All Fields First");
                return;
            }
            Requirement currentRequirement = requirementService.getRepository().findById(Long.valueOf(tabs.getSelectedTab().getId().get())).get();
            Characteristic characteristic = new Characteristic();
            characteristic.setName(txtName.getValue());
            characteristic.setValue(txtValue.getValue());
            try {
                Characteristic savedCharacteristic = characteristicService.save(characteristic);
                currentRequirement.getCharacteristics().add(savedCharacteristic);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Requirement savedRequirement = requirementService.update(currentRequirement);
                characteristicsGrid.setItems(savedRequirement.getCharacteristics());
            } catch (Exception e) {
                e.printStackTrace();
            }


            txtName.clear();
            txtValue.clear();
            dialog.close();
        });

        btnCancel.addClickListener(event-> {
            txtName.clear();
            txtValue.clear();
            dialog.close();
        });

    }

    public void addUpdateObjective() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(new H3("Add Objective"));
        TextField txtName = new TextField();
        txtName.setLabel("Name");
        TextArea txtDescription = new TextArea();
        txtDescription.setLabel("Description");
        mainLayout.add(txtName);
        mainLayout.add(txtDescription);
        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setWidthFull();
        Button btnAdd = new Button("Add");
        Button btnCancel = new Button("Cancel");
        buttonsContainer.add(btnAdd,btnCancel);
        buttonsContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(buttonsContainer);
        dialog.add(mainLayout);

        dialog.open();

        btnAdd.addClickListener(event-> {
            if(txtName.isEmpty() || txtDescription.isEmpty()) {
                Notification.show("Fill All Fields First");
                return;
            }
            Requirement currentRequirement = requirementService.getRepository().findById(Long.valueOf(tabs.getSelectedTab().getId().get())).get();
            Objective objective = new Objective();
            objective.setName(txtName.getValue());
            objective.setDescription(txtDescription.getValue());
            try {
                Objective savedObjective = objectiveService.save(objective);
                currentRequirement.getObjectives().add(savedObjective);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Requirement savedRequirement = requirementService.update(currentRequirement);
                objectivesGrid.setItems(savedRequirement.getObjectives());
            } catch (Exception e) {
                e.printStackTrace();
            }

            txtName.clear();
            txtDescription.clear();
            dialog.close();
        });

        btnCancel.addClickListener(event-> {
            txtName.clear();
            txtDescription.clear();
            dialog.close();
        });

    }

    public void initListView() {
        requirementGrid.setWidthFull();
        requirementGrid.addColumn(Requirement::getName).setHeader("Name");
        requirementGrid.addColumn(Requirement::getRequirementDescription).setHeader("Description");
        requirementGrid.addColumn(Requirement::getType).setHeader("Type");
        requirementGrid.addColumn(Requirement::getLastEdited).setHeader("Date");
        spreadSheetContainer.add(requirementGrid);
    }

    public void populateRequirementGrid() {
        requirementGrid.setItems(requirementService.getRepository().findAll());
    }


    public void populateStrategyContainer(Requirement requirement1) {
        strategyContentContainer.removeAll();
        requirement1.getStrategyAndPlan().forEach(strategy->{
            Span span = new Span(strategy);
            span.addClassName("row-span");
            span.setWidthFull();
            span.getStyle().set("border-bottom","1px solid #D3D8E0");
            strategyContentContainer.add(span);

            span.addClickListener(event-> {
                Dialog dialog = new Dialog();
                VerticalLayout mainLayout = new VerticalLayout();
                mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                mainLayout.add(new H3("Update Strategy And Plan"));
                TextArea txtPlan = new TextArea();
                txtPlan.setValue(span.getText());
                HorizontalLayout buttonsLayout = new HorizontalLayout();
                Button btnUpdate = new Button("Update");
                Button btnCancel = new Button("Cancel");
                buttonsLayout.add(btnUpdate,btnCancel);
                mainLayout.add(txtPlan,buttonsLayout);
                dialog.add(mainLayout);
                dialog.open();
                btnUpdate.addClickListener(event1-> {
                    requirement1.getStrategyAndPlan().remove(strategy);
                    requirement1.getStrategyAndPlan().add(txtPlan.getValue());
                    try {
                        requirementService.update(requirement1);
                        strategyContentContainer.remove(span);
                        Span span1 = new Span(txtPlan.getValue());
                        span1.addClassName("row-span");
                        span1.setWidthFull();
                        span1.getStyle().set("border-bottom","1px solid #D3D8E0");
                        populateStrategyContainer(requirement1);
                        dialog.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

                btnCancel.addClickListener(event1-> {
                    dialog.close();
                });
            });
        });
    }

    public void populateStakeholdersContainer(Requirement requirement1) {
        stakeholderContentContainer.removeAll();
        requirement1.getStakeholders().forEach(stakeholder->{
            Span span = new Span(stakeholder);
            span.getStyle().set("border-bottom","1px solid #D3D8E0");
            span.setWidthFull();
            span.addClassName("row-span");
            stakeholderContentContainer.add(span);

            span.addClickListener(event-> {
                Dialog dialog = new Dialog();
                VerticalLayout mainLayout = new VerticalLayout();
                mainLayout.add(new H3("Update Stakeholder"));
                mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                TextField txtStakeholder = new TextField();
                txtStakeholder.setValue(span.getText());
                HorizontalLayout buttonsLayout = new HorizontalLayout();
                Button btnUpdate = new Button("Update");
                Button btnCancel = new Button("Cancel");
                buttonsLayout.add(btnUpdate,btnCancel);
                mainLayout.add(txtStakeholder,buttonsLayout);
                dialog.add(mainLayout);
                dialog.open();
                btnUpdate.addClickListener(event1-> {
                    requirement1.getStakeholders().remove(stakeholder);
                    requirement1.getStakeholders().add(txtStakeholder.getValue());
                    try {
                        requirementService.update(requirement1);
                        stakeholderContentContainer.remove(span);
                        Span span1 = new Span(txtStakeholder.getValue());
                        span1.addClassName("row-span");
                        span1.setWidthFull();
                        span1.getStyle().set("border-bottom","1px solid #D3D8E0");
                        stakeholderContentContainer.add(span1);
                        dialog.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

                btnCancel.addClickListener(event1-> {
                    dialog.close();
                });
            });
        });
    }
}
