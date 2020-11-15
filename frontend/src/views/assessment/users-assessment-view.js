import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';

class UsersAssessmentView extends PolymerElement {

    static get template() {
        return html`
<style include="shared-styles lumo-badge lumo-typography">
                :host {
                    display: block;
                    height: 100%;
                    width: 100%;
                }
            </style>
<vaadin-horizontal-layout theme="margin" id="cardsMainLayout" style="width: 100%;"></vaadin-horizontal-layout>
`;
    }

    static get is() {
        return 'users-assessment-view';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }
}

customElements.define(UsersAssessmentView.is, UsersAssessmentView);
