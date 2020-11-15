package com.assessgo.frontend.views.assessment;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;

import com.assessgo.MainView;
import com.assessgo.backend.common.AnswerSorter;
import com.assessgo.backend.common.QuestionSorter;
import com.assessgo.backend.common.Role;
import com.assessgo.backend.entity.Answer;
import com.assessgo.backend.entity.Assessment;
import com.assessgo.backend.entity.Question;
import com.assessgo.backend.entity.Report;
import com.assessgo.backend.enums.AssessmentTypeEnum;
import com.assessgo.backend.enums.QuestionTypesEnum;
import com.assessgo.backend.repository.ReportRepository;
import com.assessgo.backend.repository.UserRepository;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.AssessmentService;
import com.assessgo.backend.service.QuestionService;
import com.assessgo.backend.service.ReportService;
import com.assessgo.backend.service.UserService;
import com.assessgo.frontend.components.FlexBoxLayout;
import com.assessgo.frontend.components.navigation.bar.AppBar;
import com.assessgo.frontend.util.LumoStyles;
import com.assessgo.frontend.util.UIUtils;
import com.assessgo.frontend.views.dashboard.DashboardView;
import com.assessgo.frontend.views.error.AccessDeniedView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "view-question", layout = MainView.class)
@PageTitle("View Question")
@CssImport("styles/views/userAssessmentView/view-question.css")
public class ViewQuestion extends Div implements HasUrlParameter<Long>, AfterNavigationObserver {

    private static final Logger LOG = LoggerFactory.getLogger(ViewQuestion.class);

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService quizService;

    private String imageBasePath = "uploadedImages";
    private Long assessmentId;
    private Assessment assessment;
    private Set<Question> tempQuestions;

    private List<Long> tempAnswers;

    private Set<Long> attemptedQuestions = new HashSet<>();
    private Set<Long> givenAnswers;


    private boolean isUserAuthorized;

    private Integer questionNo;

    private Html informationDiv;
    private Div questionDiv;
    private VerticalLayout answerDiv;
    private Div helpText;
    private Div imageDiv;
    private Image helpImage;
    private VerticalLayout imageLayout;
    private VerticalLayout questionLayout;

    private VerticalLayout fullLayout;
    private HorizontalLayout mainLayout;
    private HorizontalLayout buttonsLayout;

    private Integer counter;
    private Integer correctAnswerCounter;

    private Report quiz;
    private String typeOfAssessment;

    private ProgressBar questionProgressBar;

    int totalNoOfQuestions;



    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(assessmentId == null){
            attachEvent.getUI().navigate(DashboardView.class);
        }

    }

    public ViewQuestion() {
        setId("main-view");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        add(createContent());
    }



    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long id) {
        if (id == null) {
            beforeEvent.rerouteTo(AccessDeniedView.class);
            return;
        } else {
            assessmentId = id;
            Optional<Assessment> assessment = assessmentService.getRepository().findById(assessmentId);
            if(assessment.isPresent()) totalNoOfQuestions = assessment.get().getQuestions().size();
            if(!assessment.isPresent()){
                beforeEvent.rerouteTo(AccessDeniedView.class);
                return;
            }

            if(!isUserAuthorizedToViewQuestion(assessment)){
                beforeEvent.rerouteTo(AccessDeniedView.class);
                return;
            }
        }

        questionNo = 0;
        Optional<Assessment> assessment = assessmentService.getRepository().findById(assessmentId);
        tempQuestions = assessment.get().getQuestions();


        if(tempQuestions.isEmpty()){
            Notification.show("No Questions in this Assessment");
            beforeEvent.rerouteTo(UsersAssessmentView.class);
            return;
        }

        tempAnswers = new LinkedList<>();

        Long userId = ((UserRepository) userService.getRepository()).findByEmail(SecurityUtils.getLoggedInUsername()).getId();

        Report existingQuiz = ((ReportRepository)quizService.getRepository()).findByUserIdAndAssessmentId(userId,assessmentId);

        quiz = new Report();

        if(existingQuiz != null) {
            quiz = existingQuiz;

            for(int i = 0; i< quiz.getGivenAnswers().length; i++){
                tempAnswers.add(quiz.getGivenAnswers()[i]);

            }

            for(int i=0; i< quiz.getAttemptedQuestions().length; i++){
                attemptedQuestions.add(quiz.getAttemptedQuestions()[i]);
            }



        } else {
            quiz.setUserId(userId);
            try{
                quizService.save(quiz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(assessment.get().getAssessmentType().equals(AssessmentTypeEnum.ASSESSMENT.toString())) {
            typeOfAssessment = "assessment";
        } else {
            typeOfAssessment = "quiz";
        }

    }


    private AppBar initAppBar() {
        AppBar appBar = MainView.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(
                e -> UI.getCurrent().navigate(UsersAssessmentView.class));
        return appBar;
    }

    private Component createContent() {

        setPageContent(questionNo);

        Button nextQuestion = UIUtils.createPrimaryButton(VaadinIcon.ARROW_RIGHT);
        nextQuestion.setId("next-button");
        Button previousQuestion = UIUtils.createPrimaryButton(VaadinIcon.ARROW_LEFT);


        Button btnSubmit;
        btnSubmit = UIUtils.createPrimaryButton("Submit");
        btnSubmit.setId("submit-button");

        fullLayout = new VerticalLayout();
        fullLayout.setId("full-layout");

        buttonsLayout = new HorizontalLayout();
        buttonsLayout.setId("buttons-layout");
        buttonsLayout.add(previousQuestion,nextQuestion);

        mainLayout = new HorizontalLayout();
        mainLayout.setId("main-layout");

        if(isImageAvailable(questionNo)){
            mainLayout.add(questionLayout,imageLayout);
        } else {
            mainLayout.add(questionLayout);
        }



        VerticalLayout progressBarContainer = new VerticalLayout();

        if(totalNoOfQuestions <=1) {
            questionProgressBar = new ProgressBar(0,totalNoOfQuestions,1);
            questionProgressBar.setValue((questionNo + 1));
        } else {
            questionProgressBar = new ProgressBar(0,totalNoOfQuestions,1);
            questionProgressBar.setValue((questionNo + 1));
        }
        progressBarContainer.add(questionProgressBar);
        Text progressInfo = new Text("This is question "+ (questionNo+1) + " of "+totalNoOfQuestions);
        progressBarContainer.add(progressInfo);
        fullLayout.add(progressBarContainer,mainLayout,buttonsLayout);



        btnSubmit.addClickListener(event->{

            Dialog confirmDialog = new Dialog();
            confirmDialog.setId("confirm-dialog");
            H5 confirmTitle = new H5("Are You Sure?");
            confirmTitle.addClassNames(LumoStyles.Heading.H5);

            Text conFirmText = new Text("You are about to submit your responses. Once submitted, it can't be undone." +
                    "If you are sure, click submit button.");

            Div quizStatDiv = new Div();
            quizStatDiv.add(new H5("Total Number of Questions: "));
            quizStatDiv.add(new H3(String.valueOf(questionNo+1)));
            quizStatDiv.add(new H5("Total Number of Questions Answered: "));
            quizStatDiv.add(new H3(String.valueOf(attemptedQuestions.size())));

            FlexBoxLayout dialogAnswerButtons = new FlexBoxLayout();
            dialogAnswerButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            dialogAnswerButtons.addClassNames(LumoStyles.Padding.Top.M);

            Button btnSaveAnswer = UIUtils.createPrimaryButton("Submit");
            btnSaveAnswer.addClassName(LumoStyles.Margin.Right.XS);
            Button btnCancelAnswer = UIUtils.createErrorPrimaryButton("Cancel");
            dialogAnswerButtons.add(btnSaveAnswer,btnCancelAnswer);

            confirmDialog.add(confirmTitle,conFirmText,quizStatDiv,dialogAnswerButtons);
            btnCancelAnswer.addClickListener(event1->confirmDialog.close());
            btnSaveAnswer.addClickListener(event1 -> {

                confirmDialog.close();

                Long[] attemptedQuestionsArray;
                attemptedQuestionsArray = attemptedQuestions.stream().toArray(Long[]::new);

                Long[] tempAnswersArray;
                tempAnswersArray = tempAnswers.stream().toArray(Long[]::new);

                quiz.setAttemptedQuestions(attemptedQuestionsArray);
                quiz.setGivenAnswers(tempAnswersArray);

                quiz.setAssessmentId(assessmentId);
                quiz.setCompleted(true);
                try{
                    quizService.update(quiz);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UI.getCurrent().navigate(UserReport.class,quiz.getId());

            });

            confirmDialog.open();
        });

        nextQuestion.addClickListener( event->{
            questionNo+=1;
            removeAll();
            add(createContent());

            Long[] attemptedQuestionsArray;
            attemptedQuestionsArray = attemptedQuestions.stream().toArray(Long[]::new);

            Long[] tempAnswersArray;
            tempAnswersArray = tempAnswers.stream().toArray(Long[]::new);

            quiz.setAttemptedQuestions(attemptedQuestionsArray);
            quiz.setGivenAnswers(tempAnswersArray);
            quiz.setAssessmentId(assessmentId);
            quiz.setInProgress(true);

            try{
                quizService.update(quiz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        previousQuestion.addClickListener(event->{
            questionNo-=1;
            removeAll();
            add(createContent());

            Long[] attemptedQuestionsArray;
            attemptedQuestionsArray = attemptedQuestions.stream().toArray(Long[]::new);

            Long[] tempAnswersArray;
            tempAnswersArray = tempAnswers.stream().toArray(Long[]::new);

            quiz.setAttemptedQuestions(attemptedQuestionsArray);
            quiz.setGivenAnswers(tempAnswersArray);
            quiz.setInProgress(true);
            try{
                quizService.update(quiz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        if(questionNo == 0){
            previousQuestion.setEnabled(false);
        }

        if(questionNo == tempQuestions.size()-1){
            nextQuestion.setEnabled(false);
            nextQuestion.setVisible(false);

            buttonsLayout.add(btnSubmit);
        } else {
            nextQuestion.setVisible(true);
            nextQuestion.setEnabled(true);
            btnSubmit.setVisible(false);
        }

        initAppBar().setTitle("Question No "+(questionNo+1));

        return fullLayout;
    }


    private boolean isUserAuthorizedToViewQuestion(Optional<Assessment> tempAssessment){
        if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)){
            isUserAuthorized = true;
        } else {
            ((UserRepository)userService.getRepository()).findByAssessmentId(tempAssessment.get().getId())
                    .stream()
                    .forEach(user -> {
                        if(user.getEmail().equals(SecurityUtils.getLoggedInUsername())){
                            isUserAuthorized = true;
                        }
                    });
        }
        return isUserAuthorized;
    }


    void setPageContent(Integer no){

        Question questionContents = getList(tempQuestions).get(no);

        counter = 0;

        String msg = "";

        if(typeOfAssessment.equalsIgnoreCase("assessment")) {
            msg = String.format("<div class='information-div'><p><b>Question Group: </b> %s </p>" +
                    "<p><b>Question Type:</b> %s </p></div>",questionContents.getQuestionGroup().getFullDescription(),"Grade Contained Question");

        } else {
            msg = String.format("<div class='information-div'><p><b>Question Group: </b> %s </p>" +
                    "<p><b>Question Type:</b> %s </p></div>",questionContents.getQuestionGroup().getFullDescription(),typeOfQuestion(no));
        }

        informationDiv = new Html(msg);

        if(typeOfAssessment.equalsIgnoreCase("assessment") && questionContents.getQuestionType().equalsIgnoreCase(QuestionTypesEnum.SCALE.getValue())) {
            H4 questionHeader = new H4("Question:");
            HorizontalLayout questionTextLayout = new HorizontalLayout();
            questionTextLayout.setId("question-text-box");

           List<Answer> answerList = questionContents.getAnswers().stream()
                    .sorted(Comparator.comparing(Answer::getAnswerScore))
                    .collect(Collectors.toList());

            answerList.forEach(answer -> {
                VerticalLayout contentDiv = new VerticalLayout();
                contentDiv.addClassName("question-content-div");
                contentDiv.add(new H3(answer.getAnswerScoreName()));
                contentDiv.add(answer.getAnswer());
                questionTextLayout.add(contentDiv);
            });

            questionDiv = new Div();
            questionDiv.setId("question-div");
            Text questionText = new Text(questionContents.getQuestionText());
            questionDiv.add(questionHeader,questionText,questionTextLayout);
        } else {
            H4 questionHeader = new H4("Question:");
            Text questionText = new Text(questionContents.getQuestionText());
            questionDiv = new Div();
            questionDiv.setId("question-div");
            questionDiv.add(questionHeader,questionText);
        }

        Text helptext = new Text(questionContents.getHelpText());
        H4 helpHeader = new H4("Help:");
        helpText = new Div();
        helpText.setId("help-text");
        helpText.add(helpHeader,helptext);


        answerDiv = new VerticalLayout();
        H4 answerHeader;
        if(typeOfAssessment.equalsIgnoreCase("assessment")) {
           answerHeader = new H4("Choose One Of The following: ");
        } else {
            answerHeader = new H4("Choose correct answers: ");
        }
        answerDiv.setId("answer-div");
        answerDiv.add(answerHeader);

        if(typeOfAssessment.equalsIgnoreCase("assessment") || typeOfQuestion(no).equalsIgnoreCase("Single Answer Choice")) {
            List<Answer> tempAnswersList = questionContents.getAnswers().stream().collect(Collectors.toList());

            tempAnswersList.sort(new AnswerSorter());
            if(!typeOfAssessment.equalsIgnoreCase("assessment")) {
                Collections.shuffle(tempAnswersList);
            }
            RadioButtonGroup<Answer> answerRadioButtonGroup = new RadioButtonGroup<>();

            if(typeOfAssessment.equalsIgnoreCase("assessment") && questionContents.getQuestionType().equalsIgnoreCase(QuestionTypesEnum.SCALE.getValue())) {
                answerRadioButtonGroup.setRenderer(new TextRenderer<>(answer -> answer.getAnswerScoreName()));
            } else {
                answerRadioButtonGroup.setRenderer(new TextRenderer<>(Answer::getAnswer));
            }

            answerRadioButtonGroup.setItems(tempAnswersList);
            answerRadioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
            answerDiv.add(answerRadioButtonGroup);

            tempAnswersList.stream().forEach(answer -> {
                Optional<Long> isChecked =  tempAnswers.stream()
                        .filter(event-> event.equals(answer.getId())).findFirst();

                if(isChecked.isPresent()){
                    answerRadioButtonGroup.setValue(answer);
                }
            });

            answerRadioButtonGroup.addValueChangeListener(event -> {
                if(event.getOldValue() != null) {
                    tempAnswers.remove(event.getOldValue().getId());
                }
                tempAnswers.add(event.getValue().getId());
                attemptedQuestions.add(questionContents.getId());
            });

        } else {

            List<Answer> tempAnswersList = questionContents.getAnswers().stream().collect(Collectors.toList());
            tempAnswersList.sort(new AnswerSorter());
            Collections.shuffle(tempAnswersList);
            tempAnswersList.stream()
                .forEach(answer -> {

                    Checkbox chkAnswerAssosication = new Checkbox();
                    chkAnswerAssosication.setId(answer.getId().toString());
                    chkAnswerAssosication.setLabel(answer.getAnswer());


                    Optional<Long> isChecked =  tempAnswers.stream()
                            .filter(event-> event.equals(answer.getId())).findFirst();

                    if(isChecked.isPresent()){
                        chkAnswerAssosication.setValue(true);
                    }


                    answerDiv.add(chkAnswerAssosication);


                    chkAnswerAssosication.addValueChangeListener(event->{

                        Optional<String> optional = event.getSource().getId();
                        Long AnswerId = Long.valueOf(optional.get());
                        boolean checkedStatus = event.getValue();

                        if (checkedStatus) {
                            tempAnswers.add(AnswerId);
                            counter+=1;

                        } else {
                            tempAnswers.remove(AnswerId);
                            counter-=1;
                        }

                        if(counter > 0) {
                            attemptedQuestions.add(questionContents.getId());
                        } else {
                            attemptedQuestions.remove(questionContents.getId());
                        }

                    });
                });
        }



        try {
            String path = imageBasePath + File.separator + questionContents.getImagePath();
            StreamResource imageSrc = getImageSrc(path);
            helpImage = new Image();
            helpImage.setId("help-image");

            helpImage.setSrc(imageSrc);
            imageDiv = new Div();
            imageDiv.add(helpImage);

        } catch (IOException e) {
            imageDiv = new Div();
            imageDiv.setId("image-div");
            imageDiv.add("");
        }


        imageLayout = new VerticalLayout();
        imageLayout.setId("image-layout");
        imageLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        imageLayout.add(imageDiv);

        questionLayout = new VerticalLayout();
        questionLayout.setId("question-layout");

        if(helptext.getText().equals("")){
            questionLayout.add(informationDiv,questionDiv,answerDiv);
        } else {
            questionLayout.add(informationDiv,questionDiv,answerDiv,helpText);
        }

    }

    boolean isImageAvailable(Integer no){
        Question questionContents = getList(tempQuestions).get(no);
        if(questionContents.getImagePath() == null){
            return false;
        } else {
            return true;
        }
    }

    String typeOfQuestion(Integer no) {
        correctAnswerCounter = 0;
        Question questionContents = getList(tempQuestions).get(no);
        questionContents.getAnswers().stream()
                .forEach(answer -> {
                    if(answer.isCorrect()){
                        correctAnswerCounter++;
                    }
                });

        if(correctAnswerCounter>1){
            return "Multiple Answer Choice";
        } else {
            return "Single Answer Choice";
        }
    }

    List<Question> getList(Set<Question> questions){
        List<Question> questionList;
        questionList =  questions.stream()
                .collect(Collectors.toList());

        questionList.sort(new QuestionSorter());
        return questionList;
    }

    public static StreamResource getImageSrc(String path) throws IOException {
        byte[] bytes = StreamUtils.copyToByteArray(new FileInputStream(new File(path)));
        StreamResource streamResource = new StreamResource("",
                () -> new ByteArrayInputStream(bytes));
        return streamResource;
    }





}
