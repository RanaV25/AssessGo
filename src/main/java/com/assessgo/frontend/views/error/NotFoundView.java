package com.assessgo.frontend.views.error;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
//import com.weave.ui.util.UIUtils;
import com.assessgo.frontend.views.dashboard.DashboardView;

@HtmlImport("frontend://styles/shared-styles.html")
@Route
@PageTitle("Page Not Found")
public class NotFoundView extends Div {

    public NotFoundView() {
        getStyle().set("text-align", "center");
        H2 lbl403 = new H2("404");
        lbl403.addClassName("label-403");

        H4 lblAccessDeniedText = new H4("Page/Resource Not Found");
        H6 lblDescription = new H6("The page or resource you are trying to reach cannot be found.");

//        Button btnBackToHome = UIUtils.createButton("Back to home screen",
//                VaadinIcon.ARROW_LEFT, ButtonVariant.LUMO_TERTIARY);

        Button btnBackToHome = new Button("Back to home screen");



        btnBackToHome.getStyle().set("width", "200px");
        btnBackToHome.addClickListener(event -> {
            UI.getCurrent().navigate(DashboardView.class);
        });

        add(lbl403, lblAccessDeniedText, lblDescription, btnBackToHome);
    }
}
