package com.assessgo.frontend.views.assessment;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import com.assessgo.MainView;
import com.assessgo.backend.common.Role;
import com.assessgo.backend.entity.*;
import com.assessgo.backend.enums.AssessmentTypeEnum;
import com.assessgo.backend.enums.QuestionTypesEnum;
import com.assessgo.backend.repository.QuestionRepository;
import com.assessgo.backend.repository.ReportRepository;
import com.assessgo.backend.repository.UserRepository;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.*;
import com.assessgo.frontend.components.navigation.bar.AppBar;
import com.assessgo.frontend.views.error.AccessDeniedView;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "userReport",layout = MainView.class)
@PageTitle("Assessment Report")
@CssImport("styles/views/userAssessmentView/user-report.css")
public class UserReport extends Div implements HasUrlParameter<Long> {

    @Autowired
    private ReportService quizService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionGroupService questionGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    private Long quizId;

    private Tabs tabs;
    private Optional<Report> quiz;
    private boolean isUserAuthorized;
    private String assessmentType;

    private int x = 0;
    private int y= 0;

    private double totalScore = 0;
    private double tempScore = 0;

    private int NO_OF_CATEGORIES_ON_X_AXIS = 100;

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long id) {

        if (id == null) {
            beforeEvent.rerouteTo(AccessDeniedView.class);
            return;
        } else {
            quizId = id;
            quiz  = quizService.getRepository().findById(quizId);
            if(!quiz.isPresent()){
                beforeEvent.rerouteTo(AccessDeniedView.class);
                return;
            }

            if(!isUserAuthorizedToViewReport(quiz)){
                beforeEvent.rerouteTo(AccessDeniedView.class);
                return;
            }

            if (assessmentService.getRepository().findById(quiz.get().getAssessmentId()).get().getAssessmentType().equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.toString())) {
                assessmentType = AssessmentTypeEnum.ASSESSMENT.getValue();
            } else {
                assessmentType = AssessmentTypeEnum.QUIZ.getValue();
            }
        }
    }

    public UserReport() {
        setId("main-view");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(quizId == null){
            attachEvent.getUI().navigate(MainView.class);
        }
        initAppBar();
        add(createContent());
    }

    private AppBar initAppBar() {
        AppBar appBar = MainView.get().getAppBar();
        appBar.setTitle("Assessment Report");
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(
                e -> UI.getCurrent().navigate(UsersAssessmentView.class));
        return appBar;
    }

    private Component createContent(){

        Tab tabGroupWise = new Tab("Group Wise");
        Div pageGroupWise = new Div();

        if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN) || SecurityUtils.isUserHasRole(Role.ADMIN)) {
            pageGroupWise.add(usersGroupReport(),groupReportHeatMap(AssessmentTypeEnum.ASSESSMENT.getValue()));
        }

        Tab tabPersonalReport = new Tab("Personal Report");
        Div pagePersonalReport  = new Div();
        pagePersonalReport.setVisible(false);
        pagePersonalReport.setId("page-personal-report");


        if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
            pagePersonalReport.add(personalReportContainer(AssessmentTypeEnum.ASSESSMENT.getValue()));
        } else {
            pagePersonalReport.add(personalReportContainer(AssessmentTypeEnum.QUIZ.getValue()));
        }



        if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())){
            pagePersonalReport.add(getGroupWisePercentage());
        }


        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tabGroupWise,pageGroupWise);
        tabsToPages.put(tabPersonalReport,pagePersonalReport);

        Div pages;
        if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN) || SecurityUtils.isUserHasRole(Role.ADMIN)) {
            tabs = new Tabs(tabGroupWise,tabPersonalReport);
            pages = new Div(pageGroupWise,pagePersonalReport);
        } else {
            tabs = new Tabs(tabPersonalReport);
            pages = new Div(pagePersonalReport);
        }

        Set<Component> pagesShown = Stream.of(pageGroupWise)
                .collect(Collectors.toSet());

        if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN) || SecurityUtils.isUserHasRole(Role.ADMIN)) {
                tabs.addSelectedChangeListener(event->{
                pagesShown.forEach(page->page.setVisible(false));
                pagesShown.clear();
                Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
                selectedPage.setVisible(true);
                pagesShown.add(selectedPage);
            });
        } else {
            pagesShown.add(tabsToPages.get(tabs.getSelectedTab()));
            tabsToPages.get(tabs.getSelectedTab()).setVisible(true);
        }


        Div mainLayout = new Div();
        mainLayout.add(tabs,pages);
        return mainLayout;

    }

    private boolean isUserAuthorizedToViewReport(Optional<Report> quiz){

        if(SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)){
            isUserAuthorized = true;
        } else {
            String loggedInUser = SecurityUtils.getLoggedInUsername();
            if(quiz.get().getUserId().equals(((UserRepository)userService.getRepository()).findByEmail(loggedInUser).getId())){
                isUserAuthorized = true;
            }
        }
        return isUserAuthorized;
    }

    private Component personalReportContainer(String assessmentType) {
        Chart chart = new Chart(ChartType.HEATMAP);
        chart.setSizeFull();


        Configuration conf = chart.getConfiguration();
        conf.setTitle("Personal Report");


        Legend legend = conf.getLegend();

        if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
            legend.getTitle().setText("Question Score");
            legend.setEnabled(false);
        } else {
            legend.getTitle().setText("Question Correctness");
        }

        conf.getColorAxis().setMinColor(SolidColor.DARKORANGE);
        conf.getColorAxis().setMaxColor(SolidColor.GREENYELLOW);

        PlotOptionsHeatmap plotOptions = new PlotOptionsHeatmap();
        conf.setPlotOptions(plotOptions);

        Set<Question> questionsSet = ((QuestionRepository)(questionService.getRepository())).findAnyMatchingAgainstAssessmentId(quiz.get().getAssessmentId());
        Set<QuestionGroup> questionGroupSet = new HashSet<>();
        questionsSet.forEach(question -> {
            questionGroupSet.add(question.getQuestionGroup());
        });


        HeatSeries series = new HeatSeries();

        x = 0;
        y= 0;
        questionGroupSet.forEach(questionGroup -> {
           Set<Question> tempQuestionSet =  ((QuestionRepository)(questionService.getRepository())).findAllMatchingAgainstQuestionGroupId(questionGroup,quiz.get().getAssessmentId());
           tempQuestionSet.forEach(questionSet-> {
               if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
                   series.addHeatPoint(x,y,calculateTotalScoreOfAssessmentQuestion(quiz.get(),questionSet.getId()));
               } else {
                   series.addHeatPoint(x,y,calculatePointOfQuestion(questionSet.getId()));
               }
                y+=1;
           });
           y=0;
           x+=1;
        });

        conf.addSeries(series);

        XAxis xaxis = new XAxis();
        xaxis.setTitle("Question Group");
        questionGroupSet.forEach(questionGroup -> xaxis.addCategory(questionGroup.getQuestionGroupName()));
        conf.addxAxis(xaxis);



        YAxis yaxis = new YAxis();
        yaxis.setTitle("Question Number");


        if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
            for(int i=1; i<= NO_OF_CATEGORIES_ON_X_AXIS; i++) {
                yaxis.addCategory(String.valueOf(i));
            }
        }
        conf.addyAxis(yaxis);
        return chart;
    }

    private double calculatePointOfQuestion(Long questionId) {
        totalScore = 0;
        Optional<Question> question = questionService.getRepository().findById(questionId);
        if(question.isPresent()) {
        Set<Long> submittedAnswers = new HashSet<>(Arrays.asList(quiz.get().getGivenAnswers()));
            Set<Long> correctAnswers =  question.get().getAnswers()
                    .stream()
                    .filter(Answer::isCorrect)
                    .map(answer-> answer.getId())
                    .collect(Collectors.toSet());

            if(correctAnswers.size()> 1) {
                if(submittedAnswers.containsAll(correctAnswers)) {
                    totalScore = 2;
                } else {
                    submittedAnswers.forEach(answer-> {
                if(correctAnswers.contains(answer)) {
                    totalScore = 1;
                }
                    });
                }
            }

            if(correctAnswers.size() == 1) {
                submittedAnswers.forEach(answer-> {
                    if(correctAnswers.contains(answer)) {
                        totalScore = 2;
                    }
                });
            }
        }
        return totalScore;
    }


    private Double calculateTotalScoreOfAssessmentQuestion(Report report,Long questionId) {
        totalScore = 0;
        Optional<Question> question = questionService.getRepository().findById(questionId);
        if (question.isPresent() && (question.get().getQuestionType().equalsIgnoreCase(QuestionTypesEnum.FREE_TEXT.getValue()))
                || question.get().getQuestionType().equalsIgnoreCase(QuestionTypesEnum.SCALE.getValue())) {
            Set<Long> submittedAnswers = new HashSet<>(Arrays.asList(report.getGivenAnswers()));
            Set<Long> correctAnswers = question.get().getAnswers()
                    .stream()
                    .map(answer -> answer.getId())
                    .collect(Collectors.toSet());

            submittedAnswers.forEach(answer-> {
                if(correctAnswers.contains(answer)) {
                    totalScore += answerService.getRepository().findById(answer).get().getAnswerScore();
                }
            });

        }
        return totalScore;
    }

    public Component getGroupWisePercentage() {
        Set<Question> questionsSet = ((QuestionRepository)(questionService.getRepository())).findAnyMatchingAgainstAssessmentId(quiz.get().getAssessmentId());
        Set<QuestionGroup> questionGroupSet = new HashSet<>();
        questionsSet.forEach(question -> {
            questionGroupSet.add(question.getQuestionGroup());
        });

        HorizontalLayout mainLayout = new HorizontalLayout();
        Div div = new Div();
        div.add("Average Score: ");
        div.addClassName("average-score-content");
        mainLayout.add(div);
        mainLayout.addClassName("average-score-box-container");
        questionGroupSet.forEach(questionGroup -> {
            Set<Question> tempQuestionSet =  ((QuestionRepository)(questionService.getRepository())).findAllMatchingAgainstQuestionGroupId(questionGroup,quiz.get().getAssessmentId());
            tempScore = 0;
            tempQuestionSet.forEach(question-> {
                tempScore += calculateTotalScoreOfAssessmentQuestion(quiz.get(),question.getId());
            });
            HorizontalLayout tempLayout = new HorizontalLayout();
            tempLayout.addClassName("average-score-content");
            tempLayout.add(questionGroup.getQuestionGroupName() + ": ");
            DecimalFormat df2 = new DecimalFormat("#.##");
            tempLayout.add(df2.format(((tempScore/(tempQuestionSet.size()*100))*100)) + "%");

            mainLayout.add(tempLayout);
        });

        return mainLayout;
    }

    private Component usersGroupReport(){
        Chart chart = new Chart(ChartType.LINE);

        Configuration conf = chart.getConfiguration();
        conf.setTitle("Group Report");
        conf.getChart().setPolar(true);

        Set<Question> questionsSet = ((QuestionRepository)(questionService.getRepository())).findAnyMatchingAgainstAssessmentId(quiz.get().getAssessmentId());
        Set<QuestionGroup> questionGroupSet = new HashSet<>();
        questionsSet.forEach(question -> {
            questionGroupSet.add(question.getQuestionGroup());
        });

        Set<Report> reportSet = ((ReportRepository)reportService.getRepository()).findByAssessmentId(quiz.get().getAssessmentId());


        reportSet.forEach(report -> {
            List<Number> scoresSet = new ArrayList<>();
            questionGroupSet.forEach(questionGroup -> {
                Set<Question> tempQuestionSet = ((QuestionRepository) (questionService.getRepository())).findAllMatchingAgainstQuestionGroupId(questionGroup, quiz.get().getAssessmentId());
                tempScore = 0;
                tempQuestionSet.forEach(question -> {
                    tempScore += calculateTotalScoreOfAssessmentQuestion(report,question.getId());
                });
                double scorePercentage =  ((tempScore/(tempQuestionSet.size()*100))*100);
                scoresSet.add(scorePercentage);
            });

            ListSeries series = new ListSeries((userService.getRepository().findById(report.getUserId())).get().getEmail(),scoresSet);
            conf.addSeries(series);
        });

        XAxis xaxis = new XAxis();
        questionGroupSet.forEach(questionGroup -> xaxis.addCategory(questionGroup.getQuestionGroupName()));
        xaxis.setTickmarkPlacement(TickmarkPlacement.ON);
        conf.addxAxis(xaxis);

        YAxis yaxis = new YAxis();
        yaxis.setGridLineInterpolation("polygon");
        yaxis.setMin(0);
        yaxis.setTickInterval(10);
        yaxis.getLabels().setStep(1);
        conf.addyAxis(yaxis);

        return chart;
    }

    private Component groupReportHeatMap(String assessmentType) {
        Chart chart = new Chart(ChartType.HEATMAP);
        chart.setSizeFull();

        Configuration conf = chart.getConfiguration();
        Legend legend = conf.getLegend();


        if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
            legend.getTitle().setText("Group Wise Score In Percentage(%)");
        } else {
            legend.getTitle().setText("Question Correctness");
        }

        conf.getColorAxis().setMinColor(SolidColor.DARKORANGE);
        conf.getColorAxis().setMaxColor(SolidColor.GREENYELLOW);

        PlotOptionsHeatmap plotOptions = new PlotOptionsHeatmap();
        conf.setPlotOptions(plotOptions);

        Set<Question> questionsSet = ((QuestionRepository)(questionService.getRepository())).findAnyMatchingAgainstAssessmentId(quiz.get().getAssessmentId());
        Set<QuestionGroup> questionGroupSet = new HashSet<>();
        questionsSet.forEach(question -> {
            questionGroupSet.add(question.getQuestionGroup());
        });

        Set<Report> reportSet = ((ReportRepository)reportService.getRepository()).findByAssessmentId(quiz.get().getAssessmentId());

        HeatSeries series = new HeatSeries();

        x = 0;
        y= 0;

        reportSet.forEach(report -> {
            questionGroupSet.forEach(questionGroup -> {
                Set<Question> tempQuestionSet =  ((QuestionRepository)(questionService.getRepository())).findAllMatchingAgainstQuestionGroupId(questionGroup,quiz.get().getAssessmentId());
                tempScore = 0;
                tempQuestionSet.forEach(question-> {
                    if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
                        tempScore+= calculateTotalScoreOfAssessmentQuestion(report,question.getId());
                    }
                });
                double scorePercentage =  ((tempScore/(tempQuestionSet.size()*100))*100);
                series.addHeatPoint(x,y,scorePercentage);
                x+=1;
            });
            x=0;
            y+=1;
        });


        conf.addSeries(series);

        XAxis xaxis = new XAxis();
        xaxis.setTitle("Question Group");
        questionGroupSet.forEach(questionGroup -> xaxis.addCategory(questionGroup.getQuestionGroupName()));
        conf.addxAxis(xaxis);

        YAxis yaxis = new YAxis();
        yaxis.setTitle("User");

        if(assessmentType.equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
            reportSet.forEach(report -> {
                yaxis.addCategory((userService.getRepository().findById(report.getUserId())).get().getEmail());
            });
        }
        conf.addyAxis(yaxis);
        return chart;
    }
}



