package com.assessgo.frontend.views.diagrams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dnd.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.router.*;
import com.assessgo.MainView;
import com.assessgo.backend.dto.GraphModel;
import com.assessgo.frontend.model.DiagramsViewModel;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Route(value = "diagrams", layout = MainView.class)
@RouteAlias(value = "diagrams", layout = MainView.class)
@PageTitle("Diagrams")
@NpmPackage(value = "gojs", version = "2.1.4")
@NpmPackage(value = "@polymer/iron-icons", version = "3.0.1")
@NpmPackage(value = "@vaadin/vaadin-icons", version = "6.3.1")
@JsModule("src/views/diagrams/diagrams-view.js")
@Tag("diagrams-view")
public class DiagramView extends PolymerTemplate<DiagramsViewModel> implements AfterNavigationObserver {

    public static final Logger LOGGER = LoggerFactory.getLogger(DiagramView.class);

    @Id("tool-pallete-text")
    Div textTool;

    @Id("tool-pallete-chart")
    Div chartTool;

    @Id("editor")
    VerticalLayout editor;

    Integer blockCounter = -1;

    public DiagramView() {
        init();
    }

    private void init() {
        //Initialize drag and drop
        DragSource<Div> textToolDragSource = DragSource.create(textTool);

        textToolDragSource.addDragStartListener((DragStartEvent<Div> event) -> {
            // You can set some meta data for the drag with event.setDragData(Object)
            editor.getElement().getClassList().add("drop-area");
        });

        textToolDragSource.addDragEndListener((DragEndEvent<Div> event) -> {
            // React to drag end and possibly call event.clearDragData();
        });


        DropTarget<VerticalLayout> textToolDropTarget = DropTarget.create(editor);
        textToolDropTarget.addDropListener((DropEvent<VerticalLayout> event) -> {
            // Optional<Object> myDragData = event.getDragData();
            editor.getElement().getClassList().remove("drop-area");
            Optional<Component> dragSourceComponent = event.getDragSourceComponent();
            if (dragSourceComponent.get() == textTool) {
                RichTextEditor richTextEditor = new RichTextEditor();
                createDragableComponent(richTextEditor);
                editor.add(richTextEditor);
            } else if (dragSourceComponent.get() == chartTool) {
                Div chartBase = new Div();
                createDragableComponent(chartBase);
                chartBase.getElement().getClassList().add("chart-base");
                String id = String.format("chart-base-%d", ++blockCounter);
                chartBase.setId(id);
                editor.add(chartBase);

                ObjectMapper om = new ObjectMapper();
                try {
                    getModel().setChartId(id);
                    getModel().setData(om.writeValueAsString(map()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                editor.add(event.getDragSourceComponent().get());
            }

        });

        //Initialize drag and drop
        DragSource<Div> chartToolDragSource = DragSource.create(chartTool);

        chartToolDragSource.addDragStartListener((DragStartEvent<Div> event) -> {
            // You can set some meta data for the drag with event.setDragData(Object)
            editor.getElement().getClassList().add("drop-area");
        });

        chartToolDragSource.addDragEndListener((DragEndEvent<Div> event) -> {
            // React to drag end and possibly call event.clearDragData();
        });

    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

    }

    private JsonArray map() {

        JsonArray ja = Json.createArray();
        JsonObject jo = Json.createObject();
        jo.put("id", "");
        jo.put("name", "[ Name ]");
        jo.put("description", "[ description ]");
        jo.put("status", GraphModel.NEW);
        ja.set(ja.length(), jo);

        return ja;
    }

    private void createDragableComponent(Component component) {
        //Initialize drag and drop
        DragSource<Component> dragSource = DragSource.create(component);

        dragSource.addDragStartListener((DragStartEvent<Component> event) -> {
            // You can set some meta data for the drag with event.setDragData(Object)
            editor.getElement().getClassList().add("drop-area");
            editor.remove(component);
        });

        dragSource.addDragEndListener((DragEndEvent<Component> event) -> {
            // React to drag end and possibly call event.clearDragData();
        });

    }
}
