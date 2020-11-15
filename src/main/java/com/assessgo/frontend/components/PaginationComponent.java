package com.assessgo.frontend.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;

/**
 * A Designer generated component for the pagination-component template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("pagination-component")
@JsModule("./src/views/components/pagination-component.js")
public class PaginationComponent extends PolymerTemplate<PaginationComponent.PaginationComponentModel> {

    @Id("btnFirstPage")
    private Button btnFirstPage;
    @Id("btnPreviousPage")
    private Button btnPreviousPage;
    @Id("btnNextPage")
    private Button btnNextPage;
    @Id("btnLastPage")
    private Button btnLastPage;
    @Id("ddPageSize")
    private Select<Integer> ddPageSize;

    public int totalPages;
    public int currentPage;
    public Integer pageSize;


    public PaginationComponent() {
        ddPageSize.setItems(5,10, 15, 25, 50);
        ddPageSize.setValue(10);

    }


    public interface PaginationComponentModel extends TemplateModel {

    }

    public void resetPaginationVariable(Select<Integer> ddPageSize) {
        totalPages = 0;
        pageSize = ddPageSize.getValue();
        currentPage = 0;
    }

    /**
     * This method will enable and disable pagination buttons according to page
     *
     */
    public void setPaginationButtonsState() {

        if (currentPage >= totalPages - 1) {
            setDisableAndNotAllowedCursor(btnNextPage);
            setDisableAndNotAllowedCursor(btnLastPage);

        } else {
            setEnableAndRemoveNotAllowedCursor(btnNextPage);
            setEnableAndRemoveNotAllowedCursor(btnLastPage);
        }
        if (currentPage <= 0) {
            setDisableAndNotAllowedCursor(btnPreviousPage);
            setDisableAndNotAllowedCursor(btnFirstPage);
        } else {
            setEnableAndRemoveNotAllowedCursor(btnPreviousPage);
            setEnableAndRemoveNotAllowedCursor(btnFirstPage);
        }
    }


    /**
     * get the button and make disable and put not-allowed cursor on button
     *
     * @param btn
     */
    public void setDisableAndNotAllowedCursor(Button btn) {
        btn.setEnabled(false);
    }

    /**
     * get the button and make enable and remove not-allowed cursor on button
     *
     * @param btn
     */
    public void setEnableAndRemoveNotAllowedCursor(Button btn) {
        btn.setEnabled(true);
    }

    public Button getBtnNextPage() {
        return btnNextPage;
    }

    public Button getBtnPreviousPage() {
        return btnPreviousPage;
    }

    public Button getBtnLastPage() {
        return btnLastPage;
    }

    public Button getBtnFirstPage() {
        return btnFirstPage;
    }

    public Select<Integer> getDdPageSize() {
        return ddPageSize;
    }
}
