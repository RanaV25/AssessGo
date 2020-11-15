package com.assessgo.frontend.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.assessgo.frontend.model.TitleCardModel;

@JsModule("src/views/diagrams/diagrams-view.js")
@Tag("diagrams-view")
public class TitleCard extends PolymerTemplate<TitleCardModel> implements AfterNavigationObserver {

    @Id
    Div title;

    @Id
    Div close;

    public TitleCard(String title) {
        this.title.setText(title);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

    }
}
