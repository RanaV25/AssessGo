package com.assessgo.frontend.model;

import com.vaadin.flow.templatemodel.TemplateModel;

public interface DiagramsViewModel extends TemplateModel {
    void setData(String data);
    void setChartId(String id);
}
