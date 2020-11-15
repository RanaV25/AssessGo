import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-area.js';
import '@vaadin/vaadin-grid/src/vaadin-grid.js';
import '@vaadin/vaadin-select/src/vaadin-select.js';
import '@vaadin/vaadin-list-box/src/vaadin-list-box.js';
import '@vaadin/vaadin-item/src/vaadin-item.js';
import '@polymer/iron-icon/iron-icon.js';

class RequirementView extends PolymerElement {
    static get template() {
        return html`
<style include="shared-styles">
                :host {
                    display: block;
                    height: 100%;
                    width: 100%;
                    overflow-y: auto;
                }
            </style>
<vaadin-vertical-layout id="mainContainer" style="height:100%">
 <vaadin-horizontal-layout theme="" style="align-self: flex-end; padding: 0; margin-top:8px; justify-content: flex-end;">
  <vaadin-button theme="List View" id="btnViewSwapper" style="margin:0;">
    List View 
  </vaadin-button>
 </vaadin-horizontal-layout>
 <vaadin-vertical-layout theme="" id="viewSwapperContainer" style="align-self: stretch;width:100%;">
  <vaadin-horizontal-layout theme="margin" id="horizontalScrollView" style="align-self: stretch;margin-top:0;"></vaadin-horizontal-layout>
  <vaadin-vertical-layout theme="margin" id="detailsContainer" style="align-self: stretch; box-shadow:0 4px 8px 0 rgba(0,0,0,0.2); flex-grow: 1;">
   <vaadin-vertical-layout id="detailsCard" style="flex-grow: 1; align-self: stretch;">
    <vaadin-horizontal-layout theme="" style="align-self: stretch; justify-content: space-around;">
     <vaadin-text-field label="Name" placeholder="" id="requirementName"></vaadin-text-field>
     <vaadin-text-area label="Description" placeholder="" id="requirementDescription"></vaadin-text-area>
     <vaadin-select id="requirementType" tabindex="" label="Type"></vaadin-select>
     <vaadin-text-field label="Date" placeholder="" id="requirementLastEditedDate" readonly></vaadin-text-field>
    </vaadin-horizontal-layout>
    <vaadin-vertical-layout theme="" id="strategyContainer" style="align-self: stretch;border: 1px solid #D3D8E0">
     <h3 style="align-self: stretch;border-bottom: 1px solid #D3D8E0;">Strategy And Plan</h3>
     <vaadin-vertical-layout id="strategyContentContainer" style="align-self: stretch;"></vaadin-vertical-layout>
     <vaadin-button theme="icon" aria-label="Add new" id="btnAddStrategy" style="align-self: flex-end;">
      <iron-icon icon="lumo:plus"></iron-icon>
     </vaadin-button>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="" id="stakeholderContainer" style="align-self: stretch;border: 1px solid #D3D8E0;">
     <h3 style="align-self: stretch;border-bottom: 1px solid #D3D8E0;">Stakeholders</h3>
     <vaadin-vertical-layout id="stakeholderContentContainer" style="align-self: stretch;"></vaadin-vertical-layout>
     <vaadin-button theme="icon" aria-label="Add new" id="btnAddStakeholder" style="align-self: flex-end;">
      <iron-icon icon="lumo:plus"></iron-icon>
     </vaadin-button>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="" id="objectivesContainer" style="align-self: stretch;border: 1px solid #D3D8E0;">
     <h3 style="align-self: stretch;border-bottom: 1px solid #D3D8E0;">Objectives</h3>
     <vaadin-grid id="objectivesGrid" height-by-rows></vaadin-grid>
     <vaadin-button theme="icon" aria-label="Add new" id="btnAddObjective" style="align-self: flex-end;">
      <iron-icon icon="lumo:plus"></iron-icon>
     </vaadin-button>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="" id="characteristicsContainer" style="align-self: stretch;border: 1px solid #D3D8E0;">
     <h3 style="align-self: stretch;border-bottom: 1px solid #D3D8E0">Characteristics</h3>
     <vaadin-grid id="characteristicsGrid" height-by-rows></vaadin-grid>
     <vaadin-button theme="icon" aria-label="Add new" id="btnAddCharacteristics" style="align-self: flex-end;">
      <iron-icon icon="lumo:plus"></iron-icon>
     </vaadin-button>
    </vaadin-vertical-layout>
   </vaadin-vertical-layout>
  </vaadin-vertical-layout>
  <vaadin-vertical-layout theme="margin" id="spreadSheetContainer" style="align-self: stretch;"></vaadin-vertical-layout>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
    }

    static get is() {
        return 'requirement-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(RequirementView.is, RequirementView);
