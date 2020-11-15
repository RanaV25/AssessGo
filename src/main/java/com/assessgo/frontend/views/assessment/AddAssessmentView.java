package com.assessgo.frontend.views.assessment;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.assessgo.MainView;
import com.assessgo.backend.DummyData;
import com.assessgo.backend.common.Role;
import com.assessgo.backend.dto.*;
import com.assessgo.backend.entity.*;
import com.assessgo.backend.enums.QuestionTypesEnum;
import com.assessgo.backend.enums.AssessmentTypeEnum;
import com.assessgo.backend.repository.AccountRepository;
import com.assessgo.backend.repository.QuestionRepository;
import com.assessgo.backend.repository.UserRepository;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.*;
import com.assessgo.frontend.components.FlexBoxLayout;
import com.assessgo.frontend.components.PaginationComponent;
import com.assessgo.frontend.util.LumoStyles;
import com.assessgo.frontend.util.UIUtils;
import com.assessgo.frontend.views.error.AccessDeniedView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Tag("add-assessment-view")
@JsModule("./src/views/assessment/add-assessment-view.js")
@CssImport("styles/views/assessment/add-assessment-view.css")
@Route(value="add-assessment", layout = MainView.class)
public class AddAssessmentView extends PolymerTemplate<AddAssessmentView.AddAssessmentViewModel> implements HasUrlParameter<Long>, AfterNavigationObserver {

    @Id("questionsSplitContainer")
    private Element questionsSplitContainer;
    @Id("questionsGridContainer")
    private VerticalLayout questionsGridContainer;
    @Id("addQuestionContainer")
    private VerticalLayout addQuestionsContainer;
    @Id("btnAddQuestion")
    private Button btnAddQuestion;
    @Id("addQuestionContainerCloseBtn")
    private Button addQuestionContainerCloseBtn;
    @Id("accountsGrid")
    private Grid<AccountDto> accountsGrid;
    @Id("usersGrid")
    private Grid<UserDto> usersGrid;
    @Id("questionGrid")
    private Grid<QuestionDto> questionGrid;
    @Id("answerGrid")
    private Grid<Answer> answerGrid;
    @Id("txtAssessmentName")
    private TextField txtAssessmentName;

    @Id("txtAssessmentDescription")
    private TextArea txtAssessmentDescription;
    @Id("txtTimeLimit")
    private TextField txtTimeLimit;
    @Id("assessmentImage")
    private Image assessmentImage;
    @Id("startDate")
    private DatePicker startDate;

    @Id("endDate")
    private DatePicker endDate;
    @Id("endTime")
    private TimePicker endTime;
    @Id("startTime")
    private TimePicker startTime;
    @Id("radioAssignment")
    private RadioButtonGroup<String> radioAssignment;
    @Id("searchUsers")
    private TextField searchUsers;
    @Id("searchAccounts")
    private TextField searchAccounts;
    @Id("usersPagination")
    private PaginationComponent usersPagination;
    @Id("accountsPagination")
    private PaginationComponent accountsPagination;
    @Id("btnSaveAssessment")
    private Button btnSaveAssessment;


    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    private Binder<AssessmentDto> assessmentDtoBinder;

    private boolean isUpdateCall;

    private Long assessmentId;
    @Id("usersGridContainer")
    private VerticalLayout usersGridContainer;
    @Id("accountsGridContainer")
    private VerticalLayout accountsGridContainer;

    private Set<UserDto> tempUsersSet = new HashSet<>();
    private Set<AccountDto> tempAccountsSet = new HashSet<>();

    private Set<String> tempAssignedBy = new HashSet<>();


    @Autowired
    private RoleService roleService;

    @Id("selectType")
    private Select<String> selectType;
    @Id("btnCancelAssessment")
    private Button btnCancelAssessment;

    private LocalDate initialStartDate;
    private LocalTime initialStartTime;
    private LocalDate initialEndDate;
    private LocalTime initialEndTime;

    private byte[] bytes;
    private String imageBasePath = "uploadedImages";

    @Id("firstDiv")
    private Element firstDiv;
    @Id("assessmentImageContainer")
    private HorizontalLayout assessmentImageContainer;

    private Binder<QuestionDto> questionDtoBinder = new Binder<>();

    private ComboBox<Question> existingQuestionCombo = new ComboBox<>("Select An Existing Question");

    @Id("txtQuestionName")
    private TextField txtQuestionName;
    @Id("txtQuestionBody")
    private TextArea txtQuestionBody;

    private ComboBox<QuestionGroup> questionGroupCombo = new ComboBox<>("Question Group");

    @Id("btnAddGroup")
    private Button btnAddGroup;
    @Id("btnAddAnswer")
    private Button btnAddAnswer;
    @Id("txthelpText")
    private TextArea txthelpText;
    @Id("questionHelpImage")
    private Image questionHelpImage;
    @Id("btnAddQuestion1")
    private Button btnAddQuestion1;
    @Id("helpImageUploadContainer")
    private HorizontalLayout helpImageUploadContainer;
    @Id("btnCancelQuestion")
    private Button btnCancelQuestion;

    @Autowired
    private QuestionGroupService questionGroupService;
    @Id("groupComboBoxContainer")
    private HorizontalLayout groupComboBoxContainer;
    @Id("existingQuestionComboboxContainer")
    private HorizontalLayout existingQuestionComboboxContainer;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    private Set<Answer> tempAnswerSet = new HashSet<>();
    private Set<QuestionDto> tempQuestionsDtos = new HashSet<>();

    private byte[] helpImageBytes;
    @Id("selectQuestionType")
    private Select<String> selectQuestionType;

    private String TXT_ACCOUNT = "account";
    private String TXT_SPECIFIC = "Specific";
    private String TXT_ALL = "All";
    private String TXT_ASSOCIATION_STATUS = "Association Status";

    private Answer requestedAnswerForEdit;

    private boolean isWithinUpdate;
    private Long withInUpdateId;

    private Question currentQuestion;



    public AddAssessmentView() {

        initQuestionGrid();
        initAnswersGrid();
        initUsersGrid();
        initAccountsGrid();

        initDateListeners();
        initAssessmentImageUploader();
        initQuestionHelpImageUploader();


        btnAddQuestion1.setText("Add");

        questionGroupCombo.setWidthFull();
        groupComboBoxContainer.add(questionGroupCombo);

        existingQuestionCombo.setWidthFull();
        existingQuestionComboboxContainer.add(existingQuestionCombo);

        selectType.setItems(AssessmentTypeEnum.ASSESSMENT.toString(), AssessmentTypeEnum.SURVEY.toString(), AssessmentTypeEnum.QUIZ.toString());

        selectQuestionType.setItems(QuestionTypesEnum.SINGLE_CHOICE.getValue(),QuestionTypesEnum.MULTIPLE_CHOICE.getValue());

        usersPagination.resetPaginationVariable(usersPagination.getDdPageSize());
        paginationClickListeners(usersPagination, searchUsers, "user");


        accountsPagination.resetPaginationVariable(accountsPagination.getDdPageSize());
        paginationClickListeners(accountsPagination, searchAccounts, TXT_ACCOUNT);


        radioAssignment.setItems(TXT_ALL, TXT_SPECIFIC);
        usersGridContainer.setVisible(false);
        accountsGridContainer.setVisible(false);

        radioAssignment.addValueChangeListener(event -> {
            if (event.getValue().equals(TXT_SPECIFIC)) {
                usersGridContainer.setVisible(true);
                accountsGridContainer.setVisible(true);
            } else {
                usersGridContainer.setVisible(false);
                accountsGridContainer.setVisible(false);
            }
        });


        addQuestionsContainer.setVisible(false);


        addQuestionContainerCloseBtn.addClickListener(event -> {
            addQuestionsContainer.setVisible(false);
            questionsGridContainer.setVisible(true);
        });

        btnCancelQuestion.addClickListener(event -> {
            addQuestionsContainer.setVisible(false);
            questionsGridContainer.setVisible(true);
        });

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if (!(SecurityUtils.isUserHasRole(Role.ADMIN) || SecurityUtils.isUserHasRole(Role.SUPER_ADMIN))) {
            UI.getCurrent().navigate(AccessDeniedView.class);
        }

    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long id) {
        assessmentDtoBinder = new Binder<>(AssessmentDto.class);
        AssessmentDto assessmentDto = null;

        if (id == null) {
            assessmentDto = new AssessmentDto();
            isUpdateCall = false;

        } else {
            isUpdateCall = true;

            btnSaveAssessment.setText("Update");

            assessmentId = id;

            Optional<Assessment> assessment = assessmentService.getRepository().findById(id);
            if (!assessment.isPresent()) {
                Notification.show("No Assessment information found against id: " + id);
            }


            assessmentDto = mapper.map(assessment.get(), AssessmentDto.class);

            if (!assessment.get().getAssignedBy()[0].equalsIgnoreCase(TXT_ALL)) {
                if (assessment.get().getUsers() != null) {
                    assessment.get().getUsers().forEach(user -> tempUsersSet.add(mapper.map(user, UserDto.class)));
                }

                if (assessment.get().getAccounts() != null) {
                    assessment.get().getAccounts().forEach(account -> tempAccountsSet.add(mapper.map(account, AccountDto.class)));
                }

            }
        }

        assessmentDtoBinder.setBean(assessmentDto);

        initBindersForAssessmentForm();
        initBindersForQuestionForm();
        assessmentDtoBinderChangeEvent();

        if(id==null) {
            selectType.setValue(AssessmentTypeEnum.QUIZ.toString());
        }

        if (isUpdateCall) {
            selectType.setReadOnly(true);
            if(selectType.getValue().equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
                selectQuestionType.setItems(QuestionTypesEnum.FREE_TEXT.getValue(),QuestionTypesEnum.SCALE.getValue(),QuestionTypesEnum.SINGLE_CHOICE.getValue(),QuestionTypesEnum.MULTIPLE_CHOICE.getValue());
                selectQuestionType.setValue(QuestionTypesEnum.FREE_TEXT.getValue());
            } else {
                selectQuestionType.setItems(QuestionTypesEnum.SINGLE_CHOICE.getValue(),QuestionTypesEnum.MULTIPLE_CHOICE.getValue());
                selectQuestionType.setValue(QuestionTypesEnum.SINGLE_CHOICE.getValue());
            }
        } else {
            selectQuestionType.setValue(QuestionTypesEnum.SINGLE_CHOICE.getValue());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

        if (isUpdateCall) {
            Optional<Assessment> currentAssessment = assessmentService.getRepository().findById(assessmentId);
            User currentUser = ((UserRepository) userService.getRepository()).findByEmail(SecurityUtils.getLoggedInUsername());
            if (currentAssessment.get().getUsers().contains(currentUser) || SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)) {

            } else {
                UI.getCurrent().navigate(AccessDeniedView.class);
            }
        }

        questionGroupCombo.setItems(questionGroupService.getRepository().findAll());
        questionGroupCombo.setItemLabelGenerator(QuestionGroup::getFullDescription);

        questionGroupCombo.addValueChangeListener(event-> {
            Set<Question> questions =  ((QuestionRepository)questionService.getRepository()).findAllMatchingAgainstQuestionGroup(questionGroupCombo.getValue(),selectQuestionType.getValue());
            existingQuestionCombo.setItems(questions);
            existingQuestionCombo.setItemLabelGenerator(Question::getQuestionName);
        });


        if (isUpdateCall) {
            try {
                String path = imageBasePath + File.separator + assessmentDtoBinder.getBean().getAssessmentImagePath();
                StreamResource imageSrc = getImageSrc(path);
                assessmentImage.setSrc(imageSrc);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            assessmentImage.setSrc(DummyData.getLogo().getSrc());
        }

        populateUsersGridData(loadUsers(Optional.empty(), usersPagination.currentPage, usersPagination.pageSize), usersPagination);
        populateAccountsGridData(loadAccounts(Optional.empty(), accountsPagination.currentPage, accountsPagination.pageSize), accountsPagination);

        searchUsers.addValueChangeListener(event -> {
            String textSearch = event.getValue();
            usersPagination.resetPaginationVariable(usersPagination.getDdPageSize());
            populateUsersGridData(loadUsers(Optional.of(textSearch), usersPagination.currentPage, usersPagination.pageSize), usersPagination);
        });

        searchAccounts.addValueChangeListener(event -> {
            String textSearch = event.getValue();
            accountsPagination.resetPaginationVariable(accountsPagination.getDdPageSize());
            populateAccountsGridData(loadAccounts(Optional.of(textSearch), accountsPagination.currentPage, accountsPagination.pageSize), accountsPagination);
        });


        btnCancelAssessment.addClickListener(event -> UI.getCurrent().navigate(AssessmentView.class));

        btnSaveAssessment.addClickListener(event -> {
            saveAssessment(false);
        });

        Dialog addGroupDialog = new Dialog();
        H5 addGroupDialogTitle = new H5("Add Question Group");
        addGroupDialogTitle.addClassNames(LumoStyles.Heading.H5);

        TextField txtNewGroup = new TextField();
        txtNewGroup.setValueChangeMode(ValueChangeMode.EAGER);
        txtNewGroup.setPlaceholder("Enter Question Group Name");
        txtNewGroup.setLabel("Group Name");
        txtNewGroup.setRequired(true);
        txtNewGroup.setWidthFull();
        txtNewGroup.setRequired(true);

        TextArea txtGroupDefinition = new TextArea();
        txtGroupDefinition.setValueChangeMode(ValueChangeMode.EAGER);
        txtGroupDefinition.setPlaceholder("Enter Group Definition");
        txtGroupDefinition.setLabel("Group Definition");
        txtGroupDefinition.setRequired(true);
        txtGroupDefinition.setWidthFull();

        FormLayout addGroupForm = new FormLayout();
        addGroupForm.addClassNames(LumoStyles.Padding.Left.M);
        addGroupForm.addClassName(LumoStyles.Padding.Right.M);
        addGroupForm.add(addGroupDialogTitle);
        addGroupForm.add(txtNewGroup);
        addGroupForm.setId("add-group-form");

        FlexBoxLayout dialogButtons = new FlexBoxLayout();
        dialogButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        dialogButtons.addClassNames(LumoStyles.Padding.Top.M);

        Button btnSaveGroup = UIUtils.createPrimaryButton("Save");
        btnSaveGroup.addClassName(LumoStyles.Margin.Right.XS);
        Button btnCancelGroup = UIUtils.createErrorPrimaryButton("Cancel");
        dialogButtons.add(btnSaveGroup, btnCancelGroup);

        addGroupForm.add(txtGroupDefinition,dialogButtons);
        addGroupDialog.add(addGroupForm);


        btnAddGroup.addClickListener(event -> {
            txtGroupDefinition.clear();
            txtNewGroup.clear();
            addGroupDialog.open();
        });
        btnSaveGroup.addClickListener(event -> {
            if (!txtNewGroup.isEmpty() && !txtGroupDefinition.isEmpty()) {
                try {
                    QuestionGroup questionGroup1 = new QuestionGroup();
                    questionGroup1.setQuestionGroupName(txtNewGroup.getValue());
                    questionGroup1.setDefinition(txtGroupDefinition.getValue());
                    questionGroupService.save(questionGroup1);
                    addGroupDialog.close();
                    txtNewGroup.clear();
                    Notification.show("Question Group Added Successfully");
                    questionGroupCombo.setItems(questionGroupService.getRepository().findAll());
                    questionGroupCombo.setItemLabelGenerator(QuestionGroup::getFullDescription);
                    questionGroupCombo.setValue(questionGroup1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Fill All Required Fields First");
            }
        });

        btnCancelGroup.addClickListener(event -> addGroupDialog.close());


        Dialog addAnswerDialog = new Dialog();
        H5 addAnswerDialogTitle = new H5("Add Answer");
        addGroupDialogTitle.addClassNames(LumoStyles.Heading.H5);

        TextArea txtAnswer = new TextArea();
        txtAnswer.setId("txt-answer");
        txtAnswer.setRequired(true);
        txtAnswer.setRequiredIndicatorVisible(true);
        txtAnswer.setLabel("Answer");
        txtAnswer.setWidthFull();



        FormLayout addAnswerForm = new FormLayout();
        addAnswerForm.addClassNames(LumoStyles.Padding.Left.M);
        addAnswerForm.addClassName(LumoStyles.Padding.Right.M);
        addAnswerForm.add(addAnswerDialogTitle);


        addAnswerForm.add(txtAnswer);
        addAnswerForm.setId("answer-form");

        Checkbox isCorrect = new Checkbox();
        isCorrect.setLabel("Is Correct Answer");
        isCorrect.setValue(false);


        VerticalLayout answerScoreLayout = new VerticalLayout();
        TextField txtScoreLevelName = new TextField();
        txtScoreLevelName.setLabel("Score Level Name");
        txtScoreLevelName.setRequired(true);
        txtScoreLevelName.setWidthFull();
        NumberField txtScoreValue = new NumberField();
        txtScoreValue.setLabel("Score Value");
        txtScoreValue.setRequiredIndicatorVisible(true);
        txtScoreValue.setWidthFull();

        answerScoreLayout.add(txtScoreLevelName,txtScoreValue);


        Div answerTypeDiv = new Div();

        if (selectType.getValue().equals(AssessmentTypeEnum.QUIZ.toString()) || selectType.getValue().equals(AssessmentTypeEnum.SURVEY.toString())) {
            answerTypeDiv.removeAll();
            answerTypeDiv.add(isCorrect);
        } else {
            answerTypeDiv.removeAll();
        }


        addAnswerForm.add(answerTypeDiv);


        FlexBoxLayout dialogAnswerButtons = new FlexBoxLayout();
        dialogAnswerButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        dialogAnswerButtons.addClassNames(LumoStyles.Padding.Top.M);

        Button btnSaveAnswer = UIUtils.createPrimaryButton("Save");
        btnSaveAnswer.addClassName(LumoStyles.Margin.Right.XS);
        Button btnCancelAnswer = UIUtils.createErrorPrimaryButton("Cancel");
        dialogAnswerButtons.add(btnSaveAnswer, btnCancelAnswer);

        addAnswerForm.add(dialogAnswerButtons);
        addAnswerDialog.add(addAnswerForm);


        btnAddAnswer.addClickListener(event -> {

            btnSaveAnswer.setText("Save");
            addAnswerDialogTitle.setText("Add Answer");
            txtAnswer.clear();
            txtScoreValue.clear();
            txtScoreLevelName.clear();
            addAnswerDialog.open();

        });



        answerGrid.addItemClickListener(event-> {
            requestedAnswerForEdit = event.getItem();
            btnSaveAnswer.setText("Update");
            addAnswerDialogTitle.setText("Update Answer");
            if(event.getItem().getAnswerScore() != null) {
                txtAnswer.setValue(event.getItem().getAnswer());
                answerTypeDiv.removeAll();

                txtScoreLevelName.setValue(event.getItem().getAnswerScoreName());
                txtScoreValue.setValue(event.getItem().getAnswerScore());
                answerTypeDiv.add(answerScoreLayout);

                if(event.getItem().getAnswerScore().equals((double)0)) {
                    answerTypeDiv.removeAll();
                }
            } else {
                answerTypeDiv.removeAll();
                isCorrect.setValue(event.getItem().isCorrect());
                answerTypeDiv.add(isCorrect);
            }

            addAnswerDialog.open();

            txtAnswer.setValue(event.getItem().getAnswer());
        });


        btnSaveAnswer.addClickListener(event -> {
            try {
                Answer answer;
                if(btnSaveAnswer.getText().equalsIgnoreCase("update")){
                    answer = requestedAnswerForEdit;
                } else {
                    answer = new Answer();
                }

                answer.setAnswer(txtAnswer.getValue());

                if (selectQuestionType.getValue().equals(QuestionTypesEnum.SINGLE_CHOICE.getValue()) || selectType.getValue().equals(QuestionTypesEnum.MULTIPLE_CHOICE.getValue())) {
                    answer.setAnswerScore(null);
                    if (isCorrect.getValue()) {
                        answer.setCorrect(true);
                    } else {
                        answer.setCorrect(false);
                    }
                }

                if (selectQuestionType.getValue().equals(QuestionTypesEnum.SCALE.getValue())) {
                    answer.setAnswerScoreName(txtScoreLevelName.getValue());
                    answer.setAnswerScore(txtScoreValue.getValue());
                    answer.setCorrect(true);
                }

                if (selectQuestionType.getValue().equals(QuestionTypesEnum.SCALE.getValue())) {
                    if(txtScoreLevelName.isEmpty() || txtScoreValue.isEmpty()) {
                        Notification.show("Fill All Fields First");
                        return;
                    }
                }

                if(selectQuestionType.getValue().equalsIgnoreCase(QuestionTypesEnum.FREE_TEXT.getValue())) {
                    answer.setAnswerScore((double)0);
                    answer.setCorrect(true);
                }

                if (!txtAnswer.isEmpty()) {
                    if(btnSaveAnswer.getText().equalsIgnoreCase("save")){
                        answerService.save(answer);
                        Notification.show("Answer Added Successfully");
                    } else if(btnSaveAnswer.getText().equalsIgnoreCase("update")) {
                        answerService.update(answer);
                        Notification.show("Answer Updated Successfully");
                    }

                    tempAnswerSet.add(answer);
                    addAnswerDialog.close();
                    txtAnswer.clear();
                    isCorrect.setValue(false);

                    answerGrid.setItems(tempAnswerSet);

                } else {
                    Notification.show("Enter answer first");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnCancelAnswer.addClickListener(event -> addAnswerDialog.close());


        btnAddQuestion.addClickListener(event -> {
            tempAnswerSet.clear();
            selectQuestionType.setReadOnly(false);
            btnAddQuestion1.setText("Add");
            helpImageBytes = null;
            questionHelpImage.setSrc("");

            answerGrid.setItems(new HashSet<>());

            existingQuestionCombo.setVisible(true);

            questionsGridContainer.setVisible(false);
            addQuestionsContainer.setVisible(true);
            QuestionDto questionDto = new QuestionDto();
            questionDtoBinder.setBean(questionDto);

        });

        btnAddQuestion1.addClickListener(event1 -> {
            String successMessage = "";
            if (questionDtoBinder.isValid()) {
                try {
                    if (!tempAnswerSet.isEmpty()) {
                        Question question = mapper.map(questionDtoBinder.getBean(), Question.class);
                        question.setImagePath(getAttachedAvatarPath("questionImage", helpImageBytes, questionHelpImage));

                        if(selectType.getValue().equals(AssessmentTypeEnum.ASSESSMENT.toString())) {
                            question.setAssessmentQuestion(true);
                        } else {
                            question.setAssessmentQuestion(false);
                        }
                        question.setQuestionType(selectQuestionType.getValue());

                        question.setAnswers(tempAnswerSet);
                        answerGrid.setItems();


                        if (btnAddQuestion1.getElement().getText().equalsIgnoreCase("add")) {
                            currentQuestion = questionService.save(question);
                            successMessage = "Question Saved Successfully";
                            questionDtoBinder.setBean(new QuestionDto());
                            tempAnswerSet = new HashSet<>();
                            selectType.setReadOnly(true);

                        } else if (btnAddQuestion1.getElement().getText().equalsIgnoreCase("update")) {
                            currentQuestion = questionService.update(question);
                            successMessage = "Question updated successfully";

                            tempAnswerSet = new HashSet<>();
                        }


                        addQuestionsContainer.setVisible(false);
                        questionsGridContainer.setVisible(true);

                        Notification.show(successMessage);

                        helpImageBytes = null;

                        saveAssessment(true);

                    } else {
                        Notification.show("Add Answer First");
                        return;
                    }

                } catch (Exception e) {
                    Notification.show(e.getMessage());
                    e.printStackTrace();
                    return;
                }

            }



        });

        questionGrid.addSelectionListener(event -> {

            btnAddQuestion1.setText("Update");
            questionGrid.getDataProvider().refreshAll();
            if (event.getFirstSelectedItem().isPresent()) {
                QuestionDto questionDto = event.getFirstSelectedItem().get();
                questionDtoBinder.setBean(questionDto);

                selectQuestionType.setValue(questionDto.getQuestionType());
                selectQuestionType.setReadOnly(true);

                try {
                    String path = imageBasePath + File.separator + questionDto.getImagePath();
                    StreamResource imageSrc = getImageSrc(path);
                    questionHelpImage.setSrc(imageSrc);
                } catch (IOException e) {

                }

                existingQuestionCombo.setVisible(false);
                questionsGridContainer.setVisible(false);
                addQuestionsContainer.setVisible(true);

                Set<Answer> answerSet = ((QuestionRepository) questionService.getRepository()).findAllMatchingAgainstQuestionId(questionDto.getId());
                tempAnswerSet = answerSet;

                answerGrid.setItems(tempAnswerSet);
            }

        });

        existingQuestionCombo.addValueChangeListener(event -> {

            if (event.getValue() != null) {

                currentQuestion = event.getValue();

                addQuestionsContainer.setVisible(false);
                questionsGridContainer.setVisible(true);

                answerGrid.setItems(new HashSet<>());
                questionGrid.getDataProvider().refreshAll();

                selectType.setReadOnly(true);

                saveAssessment(true);

            }

        });


        if (isUpdateCall) {
            Set<Question> gridQuestions = ((QuestionRepository) questionService.getRepository()).findAnyMatchingAgainstAssessmentId(assessmentId);

            tempQuestionsDtos = gridQuestions
                    .stream()
                    .map(entity -> mapper.map(entity, QuestionDto.class))
                    .collect(Collectors.toSet());
            questionGrid.setItems(tempQuestionsDtos);
        }


        selectType.addValueChangeListener(event-> {
            if (event.getValue().equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())){
                selectQuestionType.setItems(QuestionTypesEnum.FREE_TEXT.getValue(),QuestionTypesEnum.SCALE.getValue(),QuestionTypesEnum.SINGLE_CHOICE.getValue(),QuestionTypesEnum.MULTIPLE_CHOICE.getValue());
                selectQuestionType.setValue(QuestionTypesEnum.FREE_TEXT.getValue());

            } else {
                selectQuestionType.setItems(QuestionTypesEnum.SINGLE_CHOICE.getValue(),QuestionTypesEnum.MULTIPLE_CHOICE.getValue());
                selectQuestionType.setValue(QuestionTypesEnum.SINGLE_CHOICE.getValue());
            }

        });



        selectQuestionType.addValueChangeListener(event-> {
            if(event.getValue() != null) {
                answerTypeDiv.removeAll();
                if(event.getValue().equals(QuestionTypesEnum.SCALE.getValue())){

                    answerTypeDiv.add(answerScoreLayout);
                } else if(event.getValue().equals(QuestionTypesEnum.MULTIPLE_CHOICE.getValue()) || (event.getValue().equals(QuestionTypesEnum.SINGLE_CHOICE.getValue()))) {
                    answerTypeDiv.add(isCorrect);
                }

                Set<Question> questions =  ((QuestionRepository)questionService.getRepository()).findAllMatchingAgainstQuestionGroup(questionGroupCombo.getValue(),selectQuestionType.getValue());
                existingQuestionCombo.setItems(questions);
                existingQuestionCombo.setItemLabelGenerator(Question::getQuestionName);
            }

        });





    }


    public interface AddAssessmentViewModel extends TemplateModel {

    }

    public void initQuestionGrid() {
        questionGrid.addColumn(new ComponentRenderer<>(questionDto -> {
            Checkbox chkQuestionAssociation = new Checkbox();
            chkQuestionAssociation.setId(questionDto.getId().toString());
            chkQuestionAssociation.setValue(true);


            chkQuestionAssociation.addValueChangeListener(event -> {
                Optional<String> optional = event.getSource().getId();
                Long accountId = Long.valueOf(optional.get());
                boolean checkedStatus = event.getValue();


                if(!checkedStatus) {
                    if(assessmentDtoBinder.isValid()) {
                        Optional<Assessment> currentAssessment = assessmentService.getRepository().findById(assessmentId);
                        if(currentAssessment.isPresent()) {
                            currentAssessment.get().getQuestions().remove(mapper.map(questionDto, Question.class));
                        }
                        try {
                            withInUpdateId = assessmentService.update(currentAssessment.get()).getId();
                            UI.getCurrent().getPage().reload();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Notification.show("Fill all the required fields first");
                    }
                }

            });

            return chkQuestionAssociation;
        })).setHeader(TXT_ASSOCIATION_STATUS).setTextAlign(ColumnTextAlign.CENTER).setWidth("60px");

        questionGrid.addColumn(QuestionDto::getQuestionName).setHeader("Question").setAutoWidth(true);
    }

    public void initAnswersGrid() {
        answerGrid.addColumn(new ComponentRenderer<>(answerDto -> {
            Checkbox chkQuestionAssociation = new Checkbox();
            chkQuestionAssociation.setId(answerDto.getId().toString());
            chkQuestionAssociation.setValue(true);


            chkQuestionAssociation.addValueChangeListener(event -> {
                Optional<String> optional = event.getSource().getId();
                Long accountId = Long.valueOf(optional.get());
                boolean checkedStatus = event.getValue();

                if (checkedStatus) {
                    tempAnswerSet.add(answerDto);
                } else {
                    tempAnswerSet.remove(answerDto);
                }

            });

            return chkQuestionAssociation;
        })).setHeader(TXT_ASSOCIATION_STATUS).setTextAlign(ColumnTextAlign.CENTER).setWidth("60px");

        answerGrid.addColumn(Answer::getAnswer).setHeader("Answer").setAutoWidth(true);
        answerGrid.addColumn(Answer::isCorrect).setHeader("Is Correct").setAutoWidth(true);

    }

    public void initUsersGrid() {
        usersGrid.addColumn(new ComponentRenderer<>(userDto -> {
            Checkbox chkUsersAssociation = new Checkbox();
            chkUsersAssociation.setId(userDto.getId().toString());

            boolean isAssociated = tempUsersSet.stream()
                    .filter(user -> user.getId() == userDto.getId())
                    .collect(Collectors.toList())
                    .size() > 0;
            chkUsersAssociation.setValue(isAssociated);


            chkUsersAssociation.addValueChangeListener(event -> {
                Optional<String> optional = event.getSource().getId();
                Long userId = Long.valueOf(optional.get());
                boolean checkedStatus = event.getValue();

                if (checkedStatus) {
                    tempUsersSet.add(userDto);
                } else {
                    tempUsersSet.remove(userDto);
                }

            });

            return chkUsersAssociation;
        })).setHeader(TXT_ASSOCIATION_STATUS).setWidth("60px");


        usersGrid.addColumn(UserDto::getFirstName).setHeader("First Name").setAutoWidth(true);
        usersGrid.addColumn(UserDto::getLastName).setHeader("Last Name").setAutoWidth(true);
        usersGrid.addColumn(UserDto::getEmail).setHeader("Email").setAutoWidth(true);
        usersGrid.addColumn(new ComponentRenderer<>(User -> {
            FlexLayout rolesLayout = new FlexLayout();
            rolesLayout.setWrapMode(FlexLayout.WrapMode.WRAP);
            User.getRoles().stream().forEach(role -> {
                Span badge = new Span(role.getRole());
                badge.getElement().setAttribute("theme", "badge success");
                badge.setId("role-badge");
                rolesLayout.add(badge);
            });
            return rolesLayout;
        })).setHeader("Roles").setAutoWidth(true);
    }

    public void initAccountsGrid() {

        accountsGrid.addColumn(new ComponentRenderer<>(accountDto -> {
            Checkbox chkAccountsAssociation = new Checkbox();
            chkAccountsAssociation.setId(accountDto.getId().toString());

            boolean isAssociated = tempAccountsSet.stream()
                    .filter(account -> account.getId() == accountDto.getId())
                    .collect(Collectors.toList())
                    .size() > 0;
            chkAccountsAssociation.setValue(isAssociated);

            chkAccountsAssociation.addValueChangeListener(event -> {
                Optional<String> optional = event.getSource().getId();
                Long accountId = Long.valueOf(optional.get());
                boolean checkedStatus = event.getValue();

                if (checkedStatus) {
                    tempAccountsSet.add(accountDto);
                } else {
                    tempAccountsSet.remove(accountDto);
                }

            });

            return chkAccountsAssociation;
        })).setHeader(TXT_ASSOCIATION_STATUS).setWidth("60px");

        accountsGrid.addColumn(AccountDto::getAccountName).setHeader("Account Name").setAutoWidth(true);
        accountsGrid.addColumn(AccountDto::getAccountDescription).setHeader("Account Description").setAutoWidth(true);
    }

    private void initBindersForAssessmentForm() {
        assessmentDtoBinder.forField(txtAssessmentName)
                .asRequired("Assessment Name is required")
                .bind(AssessmentDto::getAssessmentName, AssessmentDto::setAssessmentName);

        assessmentDtoBinder.forField(selectType)
                .asRequired("Assessment Type is Required")
                .bind(AssessmentDto::getAssessmentType, AssessmentDto::setAssessmentType);

        assessmentDtoBinder.forField(txtAssessmentDescription)
                .asRequired("Assessment Description is ")
                .bind(AssessmentDto::getAssessmentDescription, AssessmentDto::setAssessmentDescription);

        assessmentDtoBinder.forField(txtTimeLimit)
                .withValidator(text -> {

                    if (!text.isEmpty()) {
                        try {
                            double d = Double.parseDouble(txtTimeLimit.getValue());
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return true;
                }, "Please enter a number")
                .bind(AssessmentDto::getTimeLimit, AssessmentDto::setTimeLimit);

        if (isUpdateCall) {
            try {
                LocalDate localStartDate = assessmentDtoBinder.getBean().getStartDate().toLocalDate();
                startDate.setValue(localStartDate);

                LocalTime localStartTime = assessmentDtoBinder.getBean().getStartDate().toLocalTime();
                startTime.setValue(localStartTime);

                LocalDate localEndDate = assessmentDtoBinder.getBean().getEndDate().toLocalDate();
                endDate.setValue(localEndDate);

                LocalTime localEndTime = assessmentDtoBinder.getBean().getEndDate().toLocalTime();
                endTime.setValue(localEndTime);
            } catch (Exception e) {

            }


        }

        if (isUpdateCall) {
            if (assessmentDtoBinder.getBean().getAssignedBy()[0].equalsIgnoreCase(TXT_ALL)) {
                radioAssignment.setValue(TXT_ALL);
            } else {
                radioAssignment.setValue(TXT_SPECIFIC);
            }
        } else {
            radioAssignment.setValue(TXT_ALL);
        }


        txtAssessmentName.setValueChangeMode(ValueChangeMode.EAGER);
        txtAssessmentDescription.setValueChangeMode(ValueChangeMode.EAGER);
    }

    private void initBindersForQuestionForm() {
        questionDtoBinder.forField(txtQuestionName)
                .asRequired("Question Name is required")
                .bind(QuestionDto::getQuestionName, QuestionDto::setQuestionName);

        questionDtoBinder.forField(txtQuestionBody)
                .asRequired("Question is required")
                .bind(QuestionDto::getQuestionText, QuestionDto::setQuestionText);

        questionDtoBinder.forField(questionGroupCombo)
                .asRequired("Question Group is Required")
                .bind(QuestionDto::getQuestionGroup, QuestionDto::setQuestionGroup);

        questionDtoBinder.forField(txthelpText)
                .bind(QuestionDto::getHelpText, QuestionDto::setHelpText);

    }


    private void paginationClickListeners(PaginationComponent pagination, TextField searchBox, String typeOfGrid) {

        pagination.getBtnFirstPage().addClickListener(buttonClickEvent -> {
            pagination.currentPage = 0;

            if (typeOfGrid.equalsIgnoreCase("user")) {
                populateUsersGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }

            if (typeOfGrid.equalsIgnoreCase(TXT_ACCOUNT)) {
                populateAccountsGridData(loadAccounts(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }

        });

        pagination.getBtnLastPage().addClickListener(buttonClickEvent -> {
            pagination.currentPage = pagination.totalPages - 1;
            if (typeOfGrid.equalsIgnoreCase("user")) {
                populateUsersGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }

            if (typeOfGrid.equalsIgnoreCase(TXT_ACCOUNT)) {
                populateAccountsGridData(loadAccounts(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }
        });

        pagination.getBtnNextPage().addClickListener(buttonClickEvent -> {
            pagination.currentPage = pagination.currentPage + 1;
            if (typeOfGrid.equalsIgnoreCase("user")) {
                populateUsersGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }

            if (typeOfGrid.equalsIgnoreCase(TXT_ACCOUNT)) {
                populateAccountsGridData(loadAccounts(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }
        });
        pagination.getBtnPreviousPage().addClickListener(buttonClickEvent -> {
            pagination.currentPage = pagination.currentPage - 1;
            if (typeOfGrid.equalsIgnoreCase("user")) {
                populateUsersGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }

            if (typeOfGrid.equalsIgnoreCase(TXT_ACCOUNT)) {
                populateAccountsGridData(loadAccounts(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }
        });

        pagination.getDdPageSize().addValueChangeListener(event -> {
            pagination.currentPage = 0;
            pagination.pageSize = event.getValue();

            if (typeOfGrid.equalsIgnoreCase("user")) {
                populateUsersGridData(loadUsers(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }

            if (typeOfGrid.equalsIgnoreCase(TXT_ACCOUNT)) {
                populateAccountsGridData(loadAccounts(Optional.empty(), pagination.currentPage, pagination.pageSize), pagination);
            }
            searchBox.setValue("");
        });
    }


    private void populateUsersGridData(Page<User> users, PaginationComponent pagination) {
        pagination.totalPages = users.getTotalPages();


        if (SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)) {
            List<UserDto> userDtos = users.getContent()
                    .stream()
                    .map(entity -> mapper.map(entity, UserDto.class))
                    .collect(Collectors.toList());
            usersGrid.setItems(userDtos);
            pagination.setPaginationButtonsState();
        }

        if (SecurityUtils.isUserHasRole(Role.ADMIN) && (!SecurityUtils.isUserHasRole(Role.SUPER_ADMIN))) {
            List<UserDto> userDtos = new ArrayList<>();

            users.getContent()
                    .forEach(user -> {
                        userService.findByEmail(SecurityUtils.getLoggedInUsername()).getAccounts()
                                .stream()
                                .forEach(account -> {
                                    user.getAccounts().forEach(currentUserAccount -> {
                                        if (account.getId() == currentUserAccount.getId()) {
                                            userDtos.add(mapper.map(user, UserDto.class));
                                        }
                                    });
                                });
                    });

            usersGrid.setItems(userDtos);
            pagination.setPaginationButtonsState();
        }
    }

    private void populateAccountsGridData(Page<Account> accounts, PaginationComponent pagination) {
        pagination.totalPages = accounts.getTotalPages();

        List<AccountDto> accountDtos = accounts.getContent()
                .stream()
                .map(entity -> mapper.map(entity, AccountDto.class))
                .collect(Collectors.toList());

        accountsGrid.setItems(accountDtos);
        pagination.setPaginationButtonsState();
    }

    private Page<User> loadUsers(Optional<String> filter, int page, int size) {
        return userService.findAnyMatching(filter, PageRequest.of(page, size));
    }

    private Page<Account> loadAccounts(Optional<String> filter, int page, int size) {
        return accountService.findAnyMatching(filter, PageRequest.of(page, size));
    }

    private void initDateListeners() {
        if (isUpdateCall) {
            try {
                initialStartDate = assessmentDtoBinder.getBean().getStartDate().toLocalDate();
                initialStartTime = assessmentDtoBinder.getBean().getStartDate().toLocalTime();
                startDate.setValue(assessmentDtoBinder.getBean().getStartDate().toLocalDate());
                startTime.setValue(assessmentDtoBinder.getBean().getStartDate().toLocalTime());

                initialEndDate = assessmentDtoBinder.getBean().getEndDate().toLocalDate();
                initialEndTime = assessmentDtoBinder.getBean().getEndDate().toLocalTime();
                endDate.setValue(assessmentDtoBinder.getBean().getEndDate().toLocalDate());
                endTime.setValue(assessmentDtoBinder.getBean().getEndDate().toLocalTime());
            } catch (Exception e) {
                initialStartDate = null;
                initialStartTime = null;

                initialEndDate = null;
                initialEndTime = null;
            }
        }

        startDate.addValueChangeListener(event -> initialStartDate = event.getValue());
        startTime.addValueChangeListener(event -> initialStartTime = event.getValue());
        endDate.addValueChangeListener(event -> initialEndDate = event.getValue());
        endTime.addValueChangeListener(event -> initialEndTime = event.getValue());
    }

    private boolean validateForCorrectDateTime() {
        if (((initialStartDate == null) && (initialStartTime == null) && (initialEndDate == null) && (initialEndTime == null))) {
            return true;
        } else {
            try {
                LocalDateTime startDateTime = LocalDateTime.of(initialStartDate, initialStartTime);
                LocalDateTime endDateTime = LocalDateTime.of(initialEndDate, initialEndTime);

                boolean isAfter = endDateTime.isAfter(startDateTime);
                if (!isAfter) {
                    Notification.show("End Date Time Cannot be Before Current Date Time");
                    return false;
                }
            } catch (Exception e) {
                Notification.show("Please Fill all the fields of Start Time and End Time");
                return false;
            }
            return true;
        }
    }

    public void initAssessmentImageUploader() {

        Upload assessmentImageUpload = new Upload();
        assessmentImageContainer.add(assessmentImageUpload);

        assessmentImageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif", "image/jpg");
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        assessmentImageUpload.setReceiver(memoryBuffer);


        assessmentImageUpload.addFinishedListener(event -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                bytes = StreamUtils.copyToByteArray(inputStream);
                StreamResource streamResource = new StreamResource(event.getFileName(),
                        () -> new ByteArrayInputStream(bytes));
                assessmentImage.setSrc(streamResource);
            } catch (IOException e1) {
                Notification notification = new Notification(e1.getMessage());
                notification.open();
                e1.printStackTrace();
                return;
            }
        });

        assessmentImageUpload.getElement().addEventListener("file-remove", new DomEventListener() {
            @Override
            public void handleEvent(DomEvent arg0) {
                assessmentImage.setSrc(DummyData.getImageSource());
                bytes = null;
            }
        });
    }


    public String getAttachedAvatarPath(final String questionName, byte[] bytes, Image imageDisplay) throws IOException {
        String currentFileName = questionName + "_" + System.currentTimeMillis() + ".png";

        File directory = new File(imageBasePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File targetFile = new File(imageBasePath + File.separator + currentFileName);

        if (bytes == null || bytes.length < 1) {
        } else {
            FileUtils.writeByteArrayToFile(targetFile, bytes);
            return currentFileName;
        }
        return null;
    }

    public static StreamResource getImageSrc(String path) throws IOException {
        byte[] bytes = StreamUtils.copyToByteArray(new FileInputStream(new File(path)));
        StreamResource streamResource = new StreamResource("",
                () -> new ByteArrayInputStream(bytes));
        return streamResource;
    }

    public void initQuestionHelpImageUploader() {

        Upload helpImageUpload = new Upload();
        helpImageUploadContainer.add(helpImageUpload);

        helpImageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif", "image/jpg");
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        helpImageUpload.setReceiver(memoryBuffer);

        helpImageUpload.addFinishedListener(event -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                helpImageBytes = StreamUtils.copyToByteArray(inputStream);
                StreamResource streamResource = new StreamResource(event.getFileName(),
                        () -> new ByteArrayInputStream(helpImageBytes));
                questionHelpImage.setSrc(streamResource);
            } catch (IOException e1) {
                Notification notification = new Notification(e1.getMessage());
                notification.open();
                e1.printStackTrace();
                return;
            }
        });

        helpImageUpload.getElement().addEventListener("file-remove", new DomEventListener() {
            @Override
            public void handleEvent(DomEvent arg0) {
                questionHelpImage.setSrc("");
                bytes = null;
            }
        });

    }

    private boolean isSelectedTypeAssessment () {
        Boolean isAssessmentQuestion = false;

        try {
            if(selectType.getValue() != null) {
                if(selectType.getValue().equals(AssessmentTypeEnum.ASSESSMENT.toString())) {
                    isAssessmentQuestion = true;
                } else {
                    isAssessmentQuestion = false;
                }
            }
        } catch (Exception e) {

        }
        return isAssessmentQuestion;
    }



    public void assessmentDtoBinderChangeEvent() {
        assessmentDtoBinder.addStatusChangeListener(statusChangeEvent -> {
           if(assessmentDtoBinder.isValid()) {
               btnAddQuestion.setEnabled(true);
           } else {
               btnAddQuestion.setEnabled(false);
           }
        });
    }

    public void saveAssessment(boolean isWithinUpdateCall) {
            if (assessmentDtoBinder.isValid() && validateForCorrectDateTime()) {
                Assessment assessment = mapper.map(assessmentDtoBinder.getBean(), Assessment.class);
                if(isUpdateCall) {
                assessment.setQuestions(tempQuestionsDtos.stream()
                        .map(dto-> mapper.map(dto,Question.class))
                        .collect(Collectors.toSet()));
                }

                if (!radioAssignment.getValue().equalsIgnoreCase(TXT_ALL)) {

                    Set<User> userSet = new HashSet<>();

                    if (tempUsersSet.isEmpty() && tempAccountsSet.isEmpty()) {
                        Notification.show("Select Users or Accounts or Both");
                        return;
                    }

                    tempAssignedBy.clear();
                    if (!tempUsersSet.isEmpty()) {
                        tempAssignedBy.add("User");

                        Set<User> tempUserSet = tempUsersSet
                                .stream()
                                .map(userDto -> mapper.map(userDto, User.class))
                                .collect(Collectors.toSet());

                        userSet.addAll(tempUserSet);
                        userSet.add(((UserRepository) userService.getRepository()).findByEmail(SecurityUtils.getLoggedInUsername()));

                    }

                    if (!tempAccountsSet.isEmpty()) {
                        tempAssignedBy.add("Accounts");

                        Set<Account> accounts = tempAccountsSet
                                .stream()
                                .map(accountDto -> mapper.map(accountDto, Account.class))
                                .collect(Collectors.toSet());

                        assessment.setAccounts(accounts);

                        Set<User> usersByAccount = new HashSet<>();
                        accounts.forEach(account -> usersByAccount.addAll(account.getUsers()));
                        userSet.addAll(usersByAccount);

                    }

                    assessment.setUsers(userSet);
                } else {

                    if (SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)) {
                        assessment.setUsers(userService.getAllUsers());
                    } else {
                        Set<Account> associatedAccounts = ((AccountRepository) accountService.getRepository()).findAnyMatchingAgainstUserName(SecurityUtils.getLoggedInUsername());
                        Set<User> associatedUsers = new HashSet<>();

                        associatedAccounts.forEach(account -> {
                            associatedUsers.addAll(account.getUsers());
                        });

                        if (!associatedUsers.isEmpty()) {
                            assessment.setUsers(associatedUsers);
                        }
                    }


                    tempAssignedBy.add(TXT_ALL);
                }

                String[] assignedBy = new String[tempAssignedBy.size()];
                tempAssignedBy.toArray(assignedBy);

                assessment.setAssignedBy(assignedBy);

                if ((initialStartDate == null) && (initialStartTime == null) && (initialEndDate == null) && (initialEndTime == null)) {
                    assessment.setStartDate(null);
                    assessment.setEndDate(null);
                } else {
                    assessment.setStartDate(LocalDateTime.of(initialStartDate, initialStartTime));
                    assessment.setEndDate(LocalDateTime.of(initialEndDate, initialEndTime));
                }


                if (bytes == null) {
                    try {
                        String imageUrl = VaadinServletService.getCurrentServletRequest().getRequestURL().toString() + assessmentImage.getSrc();
                        bytes = IOUtils.toByteArray(new URL(imageUrl));
                    } catch (Exception e) {

                    }
                }

                try {
                    assessment.setAssessmentImagePath(getAttachedAvatarPath(assessmentDtoBinder.getBean().getAssessmentName(), bytes, assessmentImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                    if(currentQuestion != null) {
                        assessment.getQuestions().add(currentQuestion);
                    }


                if(assessment.getQuestions().isEmpty()) {
                    Notification.show("Add Some Questions");
                    return;
                }


                if (!isUpdateCall) {
                    try {
                        withInUpdateId = assessmentService.save(assessment).getId();
                        if(isWithinUpdateCall) {
                            UI.getCurrent().close();
                            UI.getCurrent().navigate(AddAssessmentView.class,withInUpdateId);
                        } else {
                            Notification.show("Assessment Saved Successfully");
                            UI.getCurrent().navigate(AssessmentView.class);
                        }
                    } catch (Exception e) {
                        Notification.show("Some Error Occurred while saving the assessment");
                    }
                } else {
                    try {
                        withInUpdateId = assessmentService.update(assessment).getId();
                        if(isWithinUpdateCall) {
                            UI.getCurrent().getPage().reload();
                        } else {
                            Notification.show("Assessment Updated Successfully");
                            UI.getCurrent().navigate(AssessmentView.class);
                        }
                    } catch (Exception e) {
                        Notification.show("Some Error Occurred while updating the assessment");
                    }
                }

            }
    }

}


