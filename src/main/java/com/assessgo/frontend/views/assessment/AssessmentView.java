package com.assessgo.frontend.views.assessment;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.assessgo.MainView;
import com.assessgo.backend.common.Role;
import com.assessgo.backend.dto.AssessmentDto;
import com.assessgo.backend.entity.Assessment;
import com.assessgo.backend.repository.AssessmentRepository;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.AssessmentService;
import com.assessgo.frontend.components.Badge;
import com.assessgo.frontend.components.PaginationComponent;
import com.assessgo.frontend.util.css.lumo.BadgeColor;
import com.assessgo.frontend.util.css.lumo.BadgeShape;
import com.assessgo.frontend.util.css.lumo.BadgeSize;
import com.assessgo.frontend.views.error.AccessDeniedView;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Tag("assessment-view")
@JsModule("./src/views/assessment/assessment-view.js")
@CssImport("styles/views/assessment/assessment-view.css")
@Route(value="assessment", layout = MainView.class)
public class AssessmentView extends PolymerTemplate<AssessmentView.AssessmentViewModel> implements AfterNavigationObserver {

    @Id("searchBox")
    private TextField searchBox;
    @Id("btnAdd")
    private Button btnAdd;
    @Id("assessmentGrid")
    private Grid<AssessmentDto> assessmentGrid;
    @Id("paginationComponent")
    private PaginationComponent pagination;

    @Autowired
    ModelMapper mapper;

    @Autowired
    AssessmentService assessmentService;


    public AssessmentView() {

        initGrid();

        pagination.resetPaginationVariable(pagination.getDdPageSize());
        paginationClickListeners(pagination);

        searchBox.setValueChangeMode(ValueChangeMode.EAGER);

        searchBox.addValueChangeListener(event -> {
            String textSearch = event.getValue();
            pagination.resetPaginationVariable(pagination.getDdPageSize());
            populateGridData(loadAssessments(Optional.of(textSearch), pagination.currentPage, pagination.pageSize));
        });

        btnAdd.addClickListener(event-> {
            UI.getCurrent().navigate(AddAssessmentView.class,null);
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if(!(SecurityUtils.isUserHasRole(Role.ADMIN) || SecurityUtils.isUserHasRole(Role.SUPER_ADMIN))) {
            UI.getCurrent().navigate(AccessDeniedView.class);
        }

    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        populateGridData(loadAssessments(Optional.empty(), pagination.currentPage, pagination.pageSize));
    }


    public interface AssessmentViewModel extends TemplateModel {

    }

    private void initGrid() {
        assessmentGrid.addComponentColumn(this::editAssessment).setHeader("Actions").setAutoWidth(true);
        assessmentGrid.addColumn(AssessmentDto::getAssessmentName).setHeader("Name").setAutoWidth(true);
        assessmentGrid.addColumn(AssessmentDto::getAssessmentType).setHeader("Type").setAutoWidth(true);
        assessmentGrid.addColumn(AssessmentDto::getAssessmentDescription).setHeader("Description").setAutoWidth(true);

        assessmentGrid.addColumn(new ComponentRenderer<>(assessmentDto -> {
            FlexLayout assignedByLayout = new FlexLayout();
            assignedByLayout.setWrapMode(FlexLayout.WrapMode.WRAP);

            List<String> assignedByList = Arrays.asList(assessmentDto.getAssignedBy());
            assignedByList.stream().forEach(assignedBy -> {
                Badge badge = new Badge(assignedBy, BadgeColor.SUCCESS, BadgeSize.S, BadgeShape.PILL);
                badge.setId("roles-badge");
                assignedByLayout.add(badge);
            });

            return assignedByLayout;
        })).setHeader("Assigned By").setAutoWidth(true);
    }

    private Component editAssessment(AssessmentDto assessmentDto) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        editButton.addClickListener(event-> {

            UI.getCurrent().navigate(AddAssessmentView.class,Long.valueOf(assessmentDto.getId()));

        });
        return editButton;
    }

    private void paginationClickListeners(PaginationComponent pagination) {

        pagination.getBtnFirstPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = 0;
            populateGridData(loadAssessments(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });

        pagination.getBtnLastPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = this.pagination.totalPages - 1;
            populateGridData(loadAssessments(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });

        pagination.getBtnNextPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = this.pagination.currentPage + 1;
            populateGridData(loadAssessments(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });
        pagination.getBtnPreviousPage().addClickListener(buttonClickEvent -> {
            this.pagination.currentPage = this.pagination.currentPage - 1;
            populateGridData(loadAssessments(Optional.empty(), pagination.currentPage, pagination.pageSize));
        });

        pagination.getDdPageSize().addValueChangeListener(event -> {
            this.pagination.currentPage = 0;
            this.pagination.pageSize = event.getValue();

            populateGridData(loadAssessments(Optional.empty(), pagination.currentPage, pagination.pageSize));
            searchBox.setValue("");
        });
    }

    private void populateGridData(Page<Assessment> assessments) {
        pagination.totalPages = assessments.getTotalPages();

        if(SecurityUtils.isUserHasRole(com.assessgo.backend.common.Role.SUPER_ADMIN)) {
            List<AssessmentDto> assessmentDtos = assessments.getContent().stream()
                    .map(entity -> mapper.map(entity, AssessmentDto.class)).collect(Collectors.toList());
            assessmentGrid.setItems(assessmentDtos);
        } else {
            List<Assessment> assessmentList = ((AssessmentRepository)assessmentService.getRepository()).findAnyMatchingAgainstUserName(SecurityUtils.getLoggedInUsername());

            List<AssessmentDto> assessmentDtos = assessmentList.stream()
                    .map(entity -> mapper.map(entity, AssessmentDto.class)).collect(Collectors.toList());
            assessmentGrid.setItems(assessmentDtos);
        }

        pagination.setPaginationButtonsState();
    }

    private Page<Assessment> loadAssessments(Optional<String> filter, int page, int size) {
        return assessmentService.findAnyMatching(filter, PageRequest.of(page, size));
    }
}
