package com.assessgo.frontend.views.assessment;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServletService;
import com.assessgo.MainView;
import com.assessgo.backend.common.AssessmentSorter;
import com.assessgo.backend.common.Role;
import com.assessgo.backend.entity.Assessment;
import com.assessgo.backend.entity.Report;
import com.assessgo.backend.enums.AssessmentTypeEnum;
import com.assessgo.backend.repository.AssessmentRepository;
import com.assessgo.backend.repository.ReportRepository;
import com.assessgo.backend.repository.UserRepository;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.AssessmentService;
import com.assessgo.backend.service.ReportService;
import com.assessgo.backend.service.UserService;
import com.assessgo.frontend.util.UIUtils;
import com.assessgo.frontend.views.dashboard.DashboardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;


@Route(value = "userAssessmentView", layout = MainView.class)
@CssImport("styles/views/userAssessmentView/user-asssessment-view.css")
@PageTitle("Assessment View")
public class UsersAssessmentView extends Div implements AfterNavigationObserver {
    private List<Assessment> assessmentList;

    @Autowired
    AssessmentService assessmentService;

    @Autowired
    ReportService quizService;

    @Autowired
    UserService userService;



    public UsersAssessmentView() {
        setClassName("main-page");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        add(assessmentCardsLayout());
    }


    private Component assessmentCardsLayout(){
        Div cardsMainLayout = new Div();
        cardsMainLayout.addClassName("cards-main-layout");


        if (SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)){
            assessmentList = assessmentService.getRepository().findAll(Sort.by(Sort.Direction.ASC, "id"));

        } else {
            assessmentList = ((AssessmentRepository)assessmentService.getRepository()).findAnyMatchingAgainstUserName(SecurityUtils.getLoggedInUsername());
        }

        if(assessmentList.isEmpty()){
            Text text = new Text("No Assessments Found");
            cardsMainLayout.add(text);

            Notification.show("No Assessment Found For This Profile");
            UI.getCurrent().navigate(DashboardView.class);
            UI.getCurrent().close();
        } else {
            assessmentList.sort(new AssessmentSorter());
            assessmentList.stream()
                    .forEach(assessment -> {

                        Div assessmentCard = new Div();
                        assessmentCard.addClassName("assessmentCard");
                        H3 title = new H3(assessment.getAssessmentName());
                        title.setClassName("title");
                        H4 type = new H4(assessment.getAssessmentType());
                        type.addClassName("type");

                        Div typeContainer = new Div();
                        typeContainer.addClassName("type-container");
                        typeContainer.add(type);

                        Text description = new Text(assessment.getAssessmentDescription());
                        Div descriptionContainer  = new Div();
                        descriptionContainer.addClassName("descriptionContainer");
                        descriptionContainer.add(description);

                        Div cardCenterContainer = new Div();
                        cardCenterContainer.addClassName("cardCenterContainer");
                        String imageUrl = VaadinServletService.getCurrentServletRequest().getRequestURL().toString() +
                                "images/" + assessment.getAssessmentImagePath();

                        assessmentCard.getStyle().set("background-image","url(\"http://localhost:8080/images/logos/1.png\")");

                        cardCenterContainer.add(typeContainer,descriptionContainer);


                        Report existingQuiz = ((ReportRepository)quizService.getRepository()).findByUserIdAndAssessmentId((((UserRepository) userService.getRepository()).findByEmail(SecurityUtils.getLoggedInUsername()).getId()),assessment.getId());
                        Boolean isCompleted;
                        if(existingQuiz != null) {
                            isCompleted = existingQuiz.isCompleted();
                        } else {
                            isCompleted = false;
                        }

                        Button btnView;
                        Button btnReView = UIUtils.createPrimaryButton("View Again");
                        btnReView.addClassName("btnView");

                        if(isCompleted){
                            btnView = UIUtils.createSuccessPrimaryButton("View Report");
                        } else {
                            btnView = UIUtils.createPrimaryButton("View");
                        }

                        btnView.addClassName("btnView");
                        Div btnContainer = new Div();
                        btnContainer.addClassName("btnContainer");
                        if(assessment.getAssessmentType().equalsIgnoreCase(AssessmentTypeEnum.ASSESSMENT.getValue())) {
                            btnContainer.add(btnView);
                            if(isCompleted) {
                                btnContainer.add(btnReView);
                            }
                        } else {
                            btnContainer.add(btnView);
                        }

                        assessmentCard.add(title,cardCenterContainer,btnContainer);
                        cardsMainLayout.add(assessmentCard);

                        btnView.addClickListener(event->{
                            if(existingQuiz != null){
                                if(existingQuiz.isCompleted()){
                                    UI.getCurrent().navigate(UserReport.class,existingQuiz.getId());
                                }

                                if(!existingQuiz.isCompleted()){
                                    UI.getCurrent().navigate(ViewQuestion.class,assessment.getId());
                                }

                            } else {
                                UI.getCurrent().navigate(ViewQuestion.class,assessment.getId());
                            }
                        });

                        btnReView.addClickListener (event -> {
                            UI.getCurrent().navigate(ViewQuestion.class,assessment.getId());
                        });
                    });
        }
        return cardsMainLayout;
    }
}