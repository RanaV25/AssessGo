import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-split-layout/src/vaadin-split-layout.js';
import '@vaadin/vaadin-date-picker/src/vaadin-date-picker.js';
import '@vaadin/vaadin-time-picker/src/vaadin-time-picker.js';
import '@vaadin/vaadin-radio-button/src/vaadin-radio-group.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '@vaadin/vaadin-grid/src/vaadin-grid.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-area.js';
import '../components/pagination-component.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-select/src/vaadin-select.js';

class AddAssessmentView extends PolymerElement {

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
 <vaadin-split-layout style="width: 100%; height: 100%;">
  <div style="width: 50%" id="firstDiv">
   <vaadin-vertical-layout theme="margin" style="align-items: center;">
    <vaadin-text-field label="Assessment Name" placeholder="" style="width: 100%" id="txtAssessmentName"></vaadin-text-field>
    <vaadin-select id="selectType" style="align-self: stretch;" label="Assessment Type"></vaadin-select>
    <vaadin-text-area label="Assessment Description" style="width: 100%; height: 200px" id="txtAssessmentDescription"></vaadin-text-area>
    <vaadin-text-field label="Time Limit (In Minutes)" placeholder="" style="width: 100%" id="txtTimeLimit"></vaadin-text-field>
    <label style="align-self: flex-start;margin-top: 8px">Assessment Image</label>
    <img id="assessmentImage" style="width:100px; height: 100px; border-radius: 10px; margin: 8px">
    <vaadin-horizontal-layout theme="margin" id="assessmentImageContainer" style="align-self: center; justify-content: center;"></vaadin-horizontal-layout>
    <vaadin-horizontal-layout theme="margin" style="width: 100%; justify-content: flex-start;">
     <vaadin-date-picker label="Start Date" style="margin-right: 8px" id="startDate"></vaadin-date-picker>
     <vaadin-time-picker label="Start Time" id="startTime"></vaadin-time-picker>
    </vaadin-horizontal-layout>
    <vaadin-horizontal-layout theme="margin" style="width: 100%; justify-content: flex-start;">
     <vaadin-date-picker label="End Date" style="margin-right: 8px" id="endDate"></vaadin-date-picker>
     <vaadin-time-picker label="End Time" id="endTime"></vaadin-time-picker>
    </vaadin-horizontal-layout>
    <vaadin-radio-group style="width: 100%" label="Assignment" id="radioAssignment"></vaadin-radio-group>
    <vaadin-vertical-layout theme="" style="flex-grow: 1; align-self: stretch;" id="usersGridContainer">
     <h4>Associate Users</h4>
     <vaadin-text-field label="" placeholder="Search" id="searchUsers"></vaadin-text-field>
     <vaadin-grid id="usersGrid"></vaadin-grid>
     <pagination-component style="flex-grow: 0; width: 100%; flex-shrink: 0;" id="usersPagination"></pagination-component>
    </vaadin-vertical-layout>
    <vaadin-vertical-layout theme="" style="align-self: stretch;" id="accountsGridContainer">
     <h4>Associate Account</h4>
     <vaadin-text-field label="" placeholder="Search" id="searchAccounts"></vaadin-text-field>
     <vaadin-grid id="accountsGrid"></vaadin-grid>
     <pagination-component id="accountsPagination" style="flex-grow: 0; width: 100%; flex-shrink: 0;"></pagination-component>
    </vaadin-vertical-layout>
    <vaadin-horizontal-layout theme="margin" style="align-self: flex-end;">
     <vaadin-button theme="primary" style="margin-right: 8px" id="btnSaveAssessment">
       Save 
     </vaadin-button>
     <vaadin-button theme="tertiary" id="btnCancelAssessment">
       Cancel 
     </vaadin-button>
    </vaadin-horizontal-layout>
   </vaadin-vertical-layout>
  </div>
  <div style="height: 100%" id="questionsSplitContainer">
   <vaadin-vertical-layout theme="margin" style="width: 90%; height: 95%" id="questionsGridContainer">
    <vaadin-horizontal-layout theme="" style="justify-content: space-between; align-self: stretch; align-items: center;">
     <h3 style="margin: 0">Questions</h3>
     <vaadin-button id="btnAddQuestion">
      <iron-icon icon="lumo:plus" slot="prefix"></iron-icon>Add 
     </vaadin-button>
    </vaadin-horizontal-layout>
    <vaadin-grid style="flex-shrink: 1; flex-grow: 1; " id="questionGrid"></vaadin-grid>
   </vaadin-vertical-layout>
   <vaadin-vertical-layout theme="" id="addQuestionContainer" style="margin: 8px">
    <vaadin-horizontal-layout style="flex-grow: 0; flex-shrink: 1; align-self: stretch; align-items: baseline;">
     <vaadin-button theme="icon" aria-label="Add new" style="margin: 0;margin-right: 8px" id="addQuestionContainerCloseBtn">
      <iron-icon icon="lumo:cross"></iron-icon>
     </vaadin-button>
     <h3>Add Question</h3>
    </vaadin-horizontal-layout>
    <vaadin-select id="selectQuestionType" style="align-self: stretch;" label="Question Type"></vaadin-select>
    <vaadin-horizontal-layout theme="" style="align-self: stretch;">
     <vaadin-horizontal-layout theme="" id="groupComboBoxContainer" style="flex-grow: 1; justify-content: center; align-self: stretch;"></vaadin-horizontal-layout>
     <vaadin-horizontal-layout theme="" style="align-self: stretch; flex-grow: 0;">
      <vaadin-button theme="icon" aria-label="Add new" style="margin-top: 35px; flex-grow: 0; margin-left: 10px; flex-shrink: 1;" id="btnAddGroup">
       <iron-icon icon="lumo:plus"></iron-icon>
      </vaadin-button>
     </vaadin-horizontal-layout>
    </vaadin-horizontal-layout>
    <vaadin-horizontal-layout theme="" id="existingQuestionComboboxContainer" style="align-self: stretch; justify-content: center;"></vaadin-horizontal-layout>
    <vaadin-text-field label="Question Name" placeholder="" style="align-self: stretch;" id="txtQuestionName"></vaadin-text-field>
    <vaadin-text-area label="Question" style="align-self: stretch; height:200px" id="txtQuestionBody"></vaadin-text-area>
    <vaadin-vertical-layout theme="" style="align-self: stretch;">
     <label>Answer</label>
     <vaadin-horizontal-layout theme="" style="align-self: stretch; align-items: center; justify-content: space-between;">
      <vaadin-grid page-size="" id="answerGrid"></vaadin-grid>
      <vaadin-button theme="icon" aria-label="Add new" style="align-self: flex-start;margin-left: 8px" id="btnAddAnswer">
       <iron-icon icon="lumo:plus"></iron-icon>
      </vaadin-button>
     </vaadin-horizontal-layout>
    </vaadin-vertical-layout>
    <vaadin-text-area label="Help Text" style="align-self: stretch;height: 200px" id="txthelpText"></vaadin-text-area>
    <vaadin-vertical-layout theme="" style="align-self: stretch;">
     <label>Help Image</label>
     <img style="width: 100px; height: 100px; border-radius: 20px; align-self: center;margin: 8px" id="questionHelpImage">
     <vaadin-horizontal-layout theme="margin" id="helpImageUploadContainer" style="justify-content: center; align-self: center;"></vaadin-horizontal-layout>
    </vaadin-vertical-layout>
    <vaadin-horizontal-layout theme="" style="align-self: flex-end; margin-top: 24px;">
     <vaadin-button theme="primary" style="margin-right: 8px" id="btnAddQuestion1">
       Add 
     </vaadin-button>
     <vaadin-button theme="tertiary" id="btnCancelQuestion">
       Cancel 
     </vaadin-button>
    </vaadin-horizontal-layout>
   </vaadin-vertical-layout>
  </div>
 </vaadin-split-layout>
</vaadin-vertical-layout>
`;
    }

    static get is() {
        return 'add-assessment-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(AddAssessmentView.is, AddAssessmentView);
