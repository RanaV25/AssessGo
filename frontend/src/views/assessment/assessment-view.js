import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-grid/src/vaadin-grid.js';
import '../components/pagination-component.js';

class AssessmentView extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles lumo-badge lumo-typography">
                :host {
                    display: block;
                    height: 100%;
                    width: 100%;
                }
            </style>
<vaadin-vertical-layout style="width: 100%; height: 100%;">
 <vaadin-horizontal-layout theme="margin" style="align-self: stretch; justify-content: space-between;">
  <vaadin-text-field label="" placeholder="Search" id="searchBox"></vaadin-text-field>
  <vaadin-button id="btnAdd">
   <iron-icon icon="lumo:plus" slot="prefix"></iron-icon>Add 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-grid style="flex-shrink: 0;  margin-left: 16px;margin-right: 16px" page-size="" theme="" id="assessmentGrid"></vaadin-grid>
 <pagination-component style="flex-shrink: 1; flex-grow: 0; align-self: stretch;" id="paginationComponent"></pagination-component>
</vaadin-vertical-layout>
`;
    }

    static get is() {
        return 'assessment-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(AssessmentView.is, AssessmentView);
