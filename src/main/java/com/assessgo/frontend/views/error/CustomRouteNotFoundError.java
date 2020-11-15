package com.assessgo.frontend.views.error;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.*;

import javax.servlet.http.HttpServletResponse;


@PageTitle("404 Not Found")
@HtmlImport("frontend://styles/shared-styles.html")
public class CustomRouteNotFoundError extends RouteNotFoundError {

    public CustomRouteNotFoundError() {
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        event.rerouteTo(NotFoundView.class);
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
