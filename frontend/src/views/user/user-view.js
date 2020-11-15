import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-lumo-styles/all-imports.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-text-field.js';
import '@polymer/iron-icon/iron-icon.js';
import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-grid/src/vaadin-grid.js';
import '../components/pagination-component.js';
import '@vaadin/vaadin-form-layout/src/vaadin-form-layout.js';
import '@vaadin/vaadin-text-field/src/vaadin-password-field.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import 'multiselect-combo-box/src/multiselect-combo-box.js';

class UserView extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles lumo-badge lumo-typography">
                :host {
                    display: block;
                    height: 100%;
                    width: 100%;
                    overflow-y: auto;
                }
            </style>
<vaadin-vertical-layout style="justify-content: center; border:height: 100vh; " id="mainContainer">
 <vaadin-horizontal-layout theme="" id="vaadinHorizontalLayout" style="margin-top: 0; margin-bottom: 0; width: 100%; justify-content: space-between; align-items: center; flex-grow: 1; flex-shrink: 1;">
  <vaadin-text-field placeholder="Search" id="txtSearch" style="flex-grow: 0; align-self: center;margin-left: 30px;">
   <iron-icon icon="lumo:search" slot="prefix"></iron-icon>
  </vaadin-text-field>
  <vaadin-horizontal-layout theme="margin" style="margin-top: 8px">
   <vaadin-button theme="icon" aria-label="Add User" id="btnAddUser">
    <iron-icon icon="lumo:plus"></iron-icon>
   </vaadin-button>
   <vaadin-button theme="icon" aria-label="Import Users" id="btnImportUser">
    <iron-icon icon="lumo:user"></iron-icon>
   </vaadin-button>
   <vaadin-button theme="icon" aria-label="Download Users" id="btnDownloadUsers">
    <iron-icon icon="lumo:download"></iron-icon>
   </vaadin-button>
  </vaadin-horizontal-layout>
 </vaadin-horizontal-layout>
 <vaadin-horizontal-layout theme="" style="width: 100%; padding: 20px; padding-top: 0; flex-grow: 1; flex-shrink: 1;">
  <vaadin-vertical-layout theme="margin" style="flex-grow: 1;flex-shrink: 1; margin-top: 0">
   <vaadin-grid id="userGrid" style="flex-shrink: 0;"></vaadin-grid>
   <pagination-component id="paginationComponent" style="width: 100%"></pagination-component>
  </vaadin-vertical-layout>
  <vaadin-form-layout id="userFormLayout" style="width: 30%; padding: 0px; flex-grow: 1; margin-top: 0;">
   <vaadin-text-field error-message="" invalid="" label="First Name" id="txtFirstName"></vaadin-text-field>
   <vaadin-text-field error-message="" invalid="" label="Last Name" id="txtLastName"></vaadin-text-field>
   <vaadin-text-field error-message="" invalid="" label="Email" id="txtEmail"></vaadin-text-field>
   <vaadin-password-field label="Password" placeholder="" value="" required id="txtPassword"></vaadin-password-field>
   <multiselect-combo-box id="role" label="Role" has-label></multiselect-combo-box>
   <multiselect-combo-box id="accountComboBox" label="Account"></multiselect-combo-box>
   <vaadin-horizontal-layout theme="margin">
    <vaadin-button theme="primary" id="btnAdd">
      Save 
    </vaadin-button>
    <vaadin-button theme="primary error" id="btnCancel" style="margin-left: 8px;">
      Cancel 
    </vaadin-button>
   </vaadin-horizontal-layout>
  </vaadin-form-layout>
 </vaadin-horizontal-layout>
</vaadin-vertical-layout>
`;
    }

    static get is() {
        return 'user-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(UserView.is, UserView);
